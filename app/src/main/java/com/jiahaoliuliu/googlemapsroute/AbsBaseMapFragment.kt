package com.jiahaoliuliu.googlemapsroute

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.jiahaoliuliu.datalayer.DirectionRepository
import com.jiahaoliuliu.entity.Coordinate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

abstract class AbsBaseMapFragment: Fragment() {

    companion object {
        // offset from edges of the map - 20% of screen
        private const val PERCENTAGE_PADDING = 20
        private const val DEFAULT_ZOOM = 15F
        private val compositeDisposable = CompositeDisposable()
    }

    @Inject lateinit var directionRepository: DirectionRepository
    protected var googleMap: GoogleMap? = null
    private var route: Polyline? = null
    private var markerBetweenLocations: Marker? = null
    private var initialLocation: Coordinate? = null
    private var initialLocationMarker: Marker? = null
    private var finalLocation: Coordinate? = null
    private var finalLocationMarker: Marker? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainApplication.getMainComponent()?.inject(this)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        if (supportMapFragment == null) {
            throw IllegalStateException("The extended fragment must have a maps in the layout")
        }
        supportMapFragment.getMapAsync {
            googleMap = it
            it.setOnCameraIdleListener {
                onMapCameraIdle()
            }

            it.setOnPoiClickListener { pointOfInterest ->
                onPointOfInterestClicked(pointOfInterest)
            }

            // Draw initial location marker
            getInitialLocation()?.let { initialLocation ->
                initialLocationMarker?.remove()
                initialLocationMarker = drawMarker(initialLocation)
            }

            // Draw final location marker
            getFinalLocation()?.let { finalLocation ->
                finalLocationMarker?.remove()
                finalLocationMarker = drawMarker(finalLocation)
            }

            // Draw the route bsetween initial location and final location if possible
            drawRouteBetweenInitialAndFinalLocations(getInitialLocation(), getFinalLocation())
            onMapSynchronized()
        }
    }

    fun drawMarker(location: Coordinate): Marker? {
        googleMap?.let {googleMapNotNull ->
            googleMapNotNull.moveCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(),
                DEFAULT_ZOOM))
            val markerOptions = MarkerOptions()
            markerOptions.position(location.toLatLng())
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            return googleMapNotNull.addMarker(markerOptions)
        }

        return null
    }

    open fun onPointOfInterestClicked(pointOfInterest: PointOfInterest) {
        // Not do anything. This method is mean to be overridden
    }

    fun getInitialLocation() = initialLocation

    fun hasInitialLocation() = getInitialLocation() != null

    open fun setInitialLocation(initialLocation: Coordinate) {
        this.initialLocation = initialLocation
        drawRouteBetweenInitialAndFinalLocations()
    }

    fun getFinalLocation() = finalLocation

    fun hasFinalLocation() = getFinalLocation() != null

    open fun setFinalLocation(finalLocation: Coordinate) {
        this.finalLocation = finalLocation
        drawRouteBetweenInitialAndFinalLocations()
    }

    abstract fun onMapSynchronized()

    open fun onMapCameraIdle() {
        // Not do anything. This method is mean to be overridden
    }

    protected fun drawRouteBetweenInitialAndFinalLocations(initialLocation: Coordinate? = this.initialLocation,
                                                           finalLocation: Coordinate? = this.finalLocation, boundMapToLocations: Boolean = true,
                                                           removePreviousRoute: Boolean = false) {
        // Preconditions
        if (initialLocation == null || finalLocation == null || googleMap == null) {
            return
        }

        // Redraw the markers
        initialLocationMarker?.remove()
        initialLocationMarker = drawMarker(initialLocation)

        val disposable = directionRepository.calculateDirection(initialLocation, finalLocation)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { showProgressScreen(true) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { showProgressScreen(false) }
            .subscribe({direction ->
                val locations = PolyUtil.decode(direction.polyline)
                // Add the lines
                if (removePreviousRoute) {
                    route?.remove()
                    markerBetweenLocations?.remove()
                }
                route = googleMap?.addPolyline(
                    PolylineOptions()
                        .color(ContextCompat.getColor(context!!, R.color.colorRoute))
                    .addAll(locations))

                // Add the marker
                markerBetweenLocations = addMarkerBetweenLocations("${direction.duration}(${direction.distance})", locations)

                // Move the camera
                if (boundMapToLocations) {
                    boundMapToLocations(direction.bounds.northeast.toLatLng(), direction.bounds.southwest.toLatLng())
                }
            },
                {throwable -> Timber.e(throwable, "Error getting the direction")}
            )
        compositeDisposable.add(disposable)
    }

    open fun showProgressScreen(showIt: Boolean) {
        // Do nothing. This method is mean to be overrided
    }

    fun boundMapToLocations(vararg locations: LatLng) {
        val width = resources.displayMetrics.widthPixels;
        val height = resources.displayMetrics.heightPixels;
        val padding = (width * PERCENTAGE_PADDING / 100)

        val boundsBuilder = LatLngBounds.builder()
        locations.iterator().forEach { boundsBuilder.include(it) }
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), width, height, padding)
        )
    }

    fun addMarkerBetweenLocations(text: String, locations: List<LatLng> ): Marker? {
        val view = activity?.layoutInflater?.inflate(R.layout.direction_marker, null, false) as TextView
        view.text = text
        val bmp = loadBitmapFromView(view)
        val midPoint = getMidPoint(locations)

        return googleMap?.addMarker(
            MarkerOptions()
                .position(midPoint)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
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

    private fun getMidPoint(locations: List<LatLng>): LatLng {
        var totalDistance = 0F
        for(i in 0 until locations.size-1) {
            totalDistance += distanceBetweenPoints(locations[i], locations[i+1])
        }

        val halfDistance = totalDistance/2
        var distance = 0F
        for(i in 0 until locations.size-1) {
            distance += distanceBetweenPoints(locations[i], locations[i+1])
            if(distance>halfDistance) {
                return LatLng( (locations[i].latitude+locations[i+1].latitude)/2, (locations[i].longitude+locations[i+1].longitude)/2)
            }
        }
        return locations[locations.size/2]
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