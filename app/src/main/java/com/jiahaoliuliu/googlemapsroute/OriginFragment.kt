package com.jiahaoliuliu.googlemapsroute

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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jiahaoliuliu.entity.Coordinate
import timber.log.Timber

class OriginFragment: AbsBaseMapFragment() {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000
        private val DEFAULT_LOCATION = LatLng(25.276, 55.296)
        private val DXB_AIRPORT_LOCATION = LatLng(25.253176, 55.365673)
        private const val DEFAULT_ZOOM = 15F
    }

    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Coordinate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_origin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        MainApplication.getMainComponent()?.inject(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // The super class will try to find the map and synchronize it
        super.onActivityCreated(savedInstanceState)
    }

    override fun onMapSynchronized() {
        getLocationPermission()
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
                    onLocationPermissionDenegated()
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

    private fun onLocationPermissionDenegated() {
        googleMap?.let {googleMapNotNull ->
            lastKnownLocation?.let { lastKnownLocationNotNull ->
                googleMapNotNull.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(lastKnownLocationNotNull.toLatLng(), DEFAULT_ZOOM))
            } ?: run {
                // if the permission is not guaranteed, then use the default location
                googleMapNotNull.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM)
                )
            }
            googleMapNotNull.uiSettings.isMyLocationButtonEnabled = false
        }
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
                    lastKnownLocation = (task.result as Location).toCoordinate()
                    setMarkerToLastKnownLocation(it)
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
        lastKnownLocation?.let {
            drawRouteBetweenOriginAndDestination(it, DXB_AIRPORT_LOCATION.toCoordinate())
        }
    }

    private fun setMarkerToLastKnownLocation(googleMap: GoogleMap) {
        lastKnownLocation?.let{
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it.toLatLng(), DEFAULT_ZOOM))

            val markerOptions = MarkerOptions()
            markerOptions.position(it.toLatLng())
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            googleMap.addMarker(markerOptions)
        }
    }
}