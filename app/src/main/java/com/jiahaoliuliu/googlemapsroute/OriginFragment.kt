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
import com.google.android.gms.maps.model.LatLng
import com.jiahaoliuliu.datalayer.DirectionRepository
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.entity.Coordinate
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
        private const val ARGUMENT_INITIAL_LOCATION = "Argument initial location"
        private const val ARGUMENT_INITIAL_PLACE_ID = "Argument place id"

        fun newInstance(initialLocation: Coordinate): OriginFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARGUMENT_INITIAL_LOCATION, initialLocation)
            val originFragment = OriginFragment()
            originFragment.arguments = bundle
            return originFragment
        }

        fun newInstance(placeId: String): OriginFragment {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_INITIAL_PLACE_ID, placeId)
            val originFragment = OriginFragment()
            originFragment.arguments = bundle
            return originFragment
        }
    }

    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentOriginBinding
    private lateinit var onSearchLocationListener: SearchLocationListener
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var initialPlaceId: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinalLocation(DirectionRepository.DXB_AIRPORT_LOCATION)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        MainApplication.getMainComponent()?.inject(this)
        arguments?.let {
            it.getParcelable<Coordinate>(ARGUMENT_INITIAL_LOCATION)?.let {initialLocation ->
                setInitialLocation(initialLocation)
            }

            if (it.containsKey(ARGUMENT_INITIAL_PLACE_ID)) {
                initialPlaceId = it.getString(ARGUMENT_INITIAL_PLACE_ID)
            }
        }
        binding.addressInput.setOnClickListener {
            onSearchLocationListener.onSearchLocationByAddressRequested(
                binding.addressInput.text.toString(), Caller.ORIGIN) }
        binding.pinLocationIcon.setOnClickListener{ onSearchLocationListener.onSearchLocationByPinRequested(Caller.ORIGIN)}
        binding.voiceSearchIcon.setOnClickListener{ onSearchLocationListener.onSearchLocationByVoiceRequested(Caller.ORIGIN)}
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // The super class will try to find the map and synchronize it
        super.onActivityCreated(savedInstanceState)
    }

    override fun onMapSynchronized() {
        // Init the map
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM)
        )

        if (!hasInitialLocation()) {
            getLocationPermission()
        } else {
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
        binding.searchLayout.visibility = View.VISIBLE
        initialPlaceId?.let {
            drawInitialLocationBasedOnInitialPlaceId(it)
        }
    }

    private fun drawInitialLocationBasedOnInitialPlaceId(initialPlaceId: String) {
        // Initial location cannot coexist with the initial location
        val disposable = placesRepository.retrievePlaceDetails(initialPlaceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ placeDetails ->
                binding.addressInput.text = placeDetails.name
                setInitialLocation(placeDetails.location)
            }, { throwable -> Timber.e(throwable, "Error retrieving place details") })
        compositeDisposable.add(disposable)
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
                    val initialLocation = (task.result as Location).toCoordinate()
                    drawMarker(initialLocation)
                    setInitialLocation(initialLocation)
                } else {
                    // TODO: Subscribe to updates from fused service
                    Timber.w(task.exception,"Current location is null. Using defaults.");
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
                    it.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        }
    }

    override fun showProgressScreen(showIt: Boolean) {
        val visibility = if (showIt) View.VISIBLE else View.GONE
        binding.progressBar.visibility = visibility
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun setInitialLocation(initialLocation: Coordinate) {
        drawMarker(initialLocation)
        directionRepository?.initialLocation = initialLocation
        super.setInitialLocation(initialLocation)
    }
}