package com.jiahaoliuliu.googlemapsroute

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.jiahaoliuliu.datalayer.DirectionRepository
import com.jiahaoliuliu.datalayer.DistanceRepository
import com.jiahaoliuliu.entity.Coordinate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class LocationFragment: Fragment() {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000
        private val DEFAULT_LOCATION = LatLng(25.276, 55.296)
        private val DXB_AIRPORT_LOCATION = LatLng(25.253176, 55.365673)
        private const val DEFAULT_ZOOM = 15F
        // offset from edges of the map - 20% of screen
        private const val PERCENTAGE_PADDING = 20
    }

    @Inject lateinit var distanceRepository: DistanceRepository
    @Inject lateinit var directionRepository: DirectionRepository
    private var googleMap: GoogleMap? = null
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
//    private var onMarkerClickListener: GoogleMap.OnMarkerClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainApplication.getMainComponent()?.inject(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        supportMapFragment.getMapAsync {
            Timber.v("Map synchronized")
            googleMap = it
            getLocationPermission()
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
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastKnownLocationNotNull.latitude, lastKnownLocationNotNull.longitude),
                        DEFAULT_ZOOM))
            } ?: run {
                // if the permission is not guaranteed, then use the default location
                googleMapNotNull.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        DEFAULT_LOCATION,
                        DEFAULT_ZOOM
                    )
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
                    lastKnownLocation = task.result
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
//            distanceRepository.calculateDistance(Coordinate(it.latitude, it.longitude),
//                Coordinate(DXB_AIRPORT_LOCATION.latitude, DXB_AIRPORT_LOCATION.longitude))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({distance -> Timber.v("Distance Returned $distance")},
//                    {throwable -> Timber.e(throwable, "Error getting the distance")}
//                )

            directionRepository.calculateDirection(Coordinate(it.latitude, it.longitude),
            Coordinate(DXB_AIRPORT_LOCATION.latitude, DXB_AIRPORT_LOCATION.longitude))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({direction ->
                    val positions = PolyUtil.decode(direction.polyline)
                    // Add the lines
                    googleMap?.addPolyline(
                        PolylineOptions()
                            .color(ContextCompat.getColor(context!!, R.color.colorRoute))
                        .addAll(positions))
                    // Add the marker
                    val view = activity?.layoutInflater?.inflate(R.layout.direction_marker, null, false) as TextView
                    view.text = "${direction.duration}(${direction.distance})"
                    val bmp = loadBitmapFromView(view)
                    val midPoint = getMidPoint(positions)
                    googleMap?.addMarker(MarkerOptions()
                        .position(midPoint)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    )

                    // Move the camera
                    val width = resources.displayMetrics.widthPixels;
                    val height = resources.displayMetrics.heightPixels;
                    val padding = (width * PERCENTAGE_PADDING/100)

                    val bounds = LatLngBounds.builder()
                        .include(LatLng(direction.bounds.northeast.latitude, direction.bounds.northeast.longitude))
                        .include(LatLng(direction.bounds.southwest.latitude, direction.bounds.southwest.longitude))
                        .build()
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
                },
                    {throwable -> Timber.e(throwable, "Error getting the direction")}
                )
        }
    }

    private fun setMarkerToLastKnownLocation(googleMap: GoogleMap) {
        val lastKnownLocationLatLng =
            LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocationLatLng, DEFAULT_ZOOM))

        val markerOptions = MarkerOptions()
        markerOptions.position(lastKnownLocationLatLng)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        googleMap.addMarker(markerOptions)
    }

    private fun loadBitmapFromView(v: View): Bitmap {
        v.measure(
            0,//View.MeasureSpec.makeMeasureSpec((resources.displayMetrics.density*30).toInt(), View.MeasureSpec.EXACTLY),
            0//View.MeasureSpec.makeMeasureSpec((resources.displayMetrics.density*30).toInt(), View.MeasureSpec.EXACTLY)
        )
        val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        val  c = Canvas(b)
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        v.draw(c)
        return b
    }

    private fun getMidPoint(positions: List<LatLng>): LatLng {
        var totalDistance = 0F
        for(i in 0 until positions.size-1) {
            totalDistance += distanceBetweenPoints(positions[i], positions[i+1])
        }

        val halfDistance = totalDistance/2
        var distance = 0F
        for(i in 0 until positions.size-1) {
            distance += distanceBetweenPoints(positions[i], positions[i+1])
            if(distance>halfDistance) {
                return LatLng( (positions[i].latitude+positions[i+1].latitude)/2, (positions[i].longitude+positions[i+1].longitude)/2)
            }
        }
        return positions[positions.size/2]
    }

    private fun distanceBetweenPoints(p1: LatLng, p2: LatLng): Float {
        return calculateDistanceDistance(p1.latitude, p1.longitude, p2.latitude, p2.longitude).toFloat()
    }
    private fun calculateDistanceDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return if (lat1 == lat2 && lon1 == lon2) {
            0.0
        } else {
            val theta = lon1 - lon2
            var dist = sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(Math.toRadians(theta))
            dist = acos(dist)
            dist = Math.toDegrees(dist)
            dist *= 60 * 1.1515
            dist *= 1.609344
            dist * 1000
        }
    }
}