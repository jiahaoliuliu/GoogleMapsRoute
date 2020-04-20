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
    }

    @Inject lateinit var directionRepository: DirectionRepository
    protected var googleMap: GoogleMap? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainApplication.getMainComponent()?.inject(this)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        if (supportMapFragment == null) {
            throw IllegalStateException("The extended fragment must have a maps in the layout")
        }
        supportMapFragment.getMapAsync {
            googleMap = it
            onMapSynchronized()
        }
    }

    abstract fun onMapSynchronized()

    protected fun drawRouteBetweenOriginAndDestination(origin: Coordinate, destination: Coordinate, boundMapToLocations: Boolean = true) {
        val disposable = directionRepository.calculateDirection(origin, destination)
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
                if (boundMapToLocations) {
                    boundMapToLocations(direction.bounds.northeast.toLatLng(), direction.bounds.southwest.toLatLng())
                }
            },
                {throwable -> Timber.e(throwable, "Error getting the direction")}
            )
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