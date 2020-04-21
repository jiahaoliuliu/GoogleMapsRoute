package com.jiahaoliuliu.googlemapsroute

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jiahaoliuliu.datalayer.DirectionRepository
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.googlemapsroute.LocationSearchFragment.Caller
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentOriginBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class OriginFragment: AbsBaseMapFragment() {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000
        private val DEFAULT_LOCATION = LatLng(25.276, 55.296)
        private const val DEFAULT_ZOOM = 15F
        private val compositeDisposable = CompositeDisposable()
    }

    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentOriginBinding
    private lateinit var onSearchLocationListener: SearchLocationListener
    private var isLocationPermissionAlreadyAskedToUser = false
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var initialLocation: Coordinate? = null
    private var initialLocationMarker: Marker? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchLocationListener) {
            onSearchLocationListener = context
        } else {
            throw ClassCastException("The parent activity must implement SearchLocationListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentOriginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        MainApplication.getMainComponent()?.inject(this)
        binding.addressInput.setOnClickListener {
            onSearchLocationListener.onSearchLocationByAddressRequested(
                binding.addressInput.text.toString(), Caller.ORIGIN) }
        binding.pinLocationIcon.setOnClickListener{ onSearchLocationListener.onSearchLocationByPinRequested()}
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // The super class will try to find the map and synchronize it
        super.onActivityCreated(savedInstanceState)
    }

    override fun onMapSynchronized() {
        // Init the map
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM)
        )

        if (!isLocationPermissionAlreadyAskedToUser) {
            isLocationPermissionAlreadyAskedToUser = true
            getLocationPermission()
        } else {
            initialLocation?.let {
                setMarkerToInitialLocation()
                boundMapToLocations(it.toLatLng(), DirectionRepository.DXB_AIRPORT_LOCATION.toLatLng())
                drawRouteBetweenOriginAndDestination(
                    it, DirectionRepository.DXB_AIRPORT_LOCATION, false
                )
            }

            binding.searchLayout.visibility = View.VISIBLE
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onLocationPermissionGuaranteed()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when(requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLocationPermissionGuaranteed()
                } else {
                    onLocationPermissionNegated()
                }
            }
        }
    }

    private fun onLocationPermissionGuaranteed() {
        locationPermissionGranted = true
        // Turn on the my location layer and the related control on thee map
        updateLocationUI()
        // Get the current location of the device and set the position on thee map
        getDeviceLocation();
    }

    private fun onLocationPermissionNegated() {
        googleMap?.let {googleMapNotNull ->
            initialLocation?.let { lastKnownLocationNotNull ->
                googleMapNotNull.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(lastKnownLocationNotNull.toLatLng(), DEFAULT_ZOOM))
            }
            googleMapNotNull.uiSettings.isMyLocationButtonEnabled = false
        }

        binding.searchLayout.visibility = View.VISIBLE
    }

    private fun updateLocationUI() {
        googleMap?.let {
            it.isMyLocationEnabled = true
            it.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        googleMap?.let {
            if (!locationPermissionGranted) {
                return
            }
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    initialLocation = (task.result as Location).toCoordinate()
                    directionRepository.initialLocation = initialLocation
                    setMarkerToInitialLocation()
                    drawDistanceToTheAirport()
                } else {
                    // TODO: Subscribe to updates from fused service
                    Timber.w(task.exception,"Current location is null. Using defaults.");
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
                    it.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        }
    }

    private fun drawDistanceToTheAirport() {
        initialLocation?.let {
            drawRouteBetweenOriginAndDestination(it, DirectionRepository.DXB_AIRPORT_LOCATION)
        }
    }

    private fun setMarkerToInitialLocation() {
        googleMap?.let {googleMapNotNull ->
            initialLocation?.let {
                initialLocationMarker?.remove()
                googleMapNotNull.moveCamera(CameraUpdateFactory.newLatLngZoom(it.toLatLng(), DEFAULT_ZOOM))
                val markerOptions = MarkerOptions()
                markerOptions.position(it.toLatLng())
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                initialLocationMarker = googleMapNotNull.addMarker(markerOptions)
            }
        }
    }

    fun showRouteFromLocation(placeId: String) {
        val disposable = placesRepository.retrievePlaceDetails(placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ placeDetails ->
                binding.addressInput.text = placeDetails.name
                initialLocation = placeDetails.location
                directionRepository.initialLocation = initialLocation
            }, { throwable -> Timber.e(throwable, "Error retrieving place details") })
        compositeDisposable.add(disposable)
    }

    fun showRouteFromLocation(location: Coordinate) {
        initialLocation = location
        directionRepository.initialLocation = initialLocation
        binding.searchLayout.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}