package com.jiahaoliuliu.googlemapsroute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jiahaoliuliu.datalayer.DirectionRepository
import com.jiahaoliuliu.datalayer.DistanceRepository
import timber.log.Timber
import javax.inject.Inject

class DestinationFragment: Fragment() {

    companion object {
        private val BALI_AIRPORT_LOCATION = LatLng(-8.744000, 115.174858)
        private const val DEFAULT_ZOOM = 15F
        // offset from edges of the map - 20% of screen
        private const val PERCENTAGE_PADDING = 20
    }

    @Inject lateinit var distanceRepository: DistanceRepository
    @Inject lateinit var directionRepository: DirectionRepository
    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainApplication.getMainComponent()?.inject(this)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        supportMapFragment.getMapAsync {
            Timber.v("Map synchronized")
            googleMap = it
            showAirportLocation()
        }
    }

    private fun showAirportLocation() {
        googleMap?.let {googleMapNotNull ->
            val markerOptions = MarkerOptions()
            markerOptions.position(BALI_AIRPORT_LOCATION)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            googleMapNotNull.addMarker(markerOptions)

            googleMapNotNull.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(BALI_AIRPORT_LOCATION.latitude, BALI_AIRPORT_LOCATION.longitude),
                    DEFAULT_ZOOM))
        }
    }
}