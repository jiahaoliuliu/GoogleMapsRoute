package com.jiahaoliuliu.googlemapsroute

import android.content.Context
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
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentDestinationBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DestinationFragment: Fragment() {

    companion object {
        private val BALI_AIRPORT_LOCATION = LatLng(-8.744000, 115.174858)
        private const val DEFAULT_ZOOM = 15F
        // offset from edges of the map - 20% of screen
        private const val PERCENTAGE_PADDING = 20
        private const val TIME_DIFFERENCE_FOR_INPUT = 1000L
    }

    @Inject
    lateinit var directionRepository: DirectionRepository
    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentDestinationBinding
    private var googleMap: GoogleMap? = null
    private lateinit var onSearchLocationListener: SearchLocationListener
    private val compositeDisposable = CompositeDisposable()

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
        binding = FragmentDestinationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainApplication.getMainComponent()?.inject(this)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        supportMapFragment.getMapAsync {
            Timber.v("Map synchronized")
            googleMap = it
            showAirportLocation()
            binding.addressInput.visibility = View.VISIBLE
        }
        binding.addressInput.setOnClickListener {
            onSearchLocationListener?.onSearchLocationByAddressRequested(binding.addressInput.text.toString()) }
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

    fun showRouteToLocation(placeId: String) {
        val disposable = placesRepository.retrievePlaceDetails(placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ placeDetails ->
                Timber.v("Place details $placeDetails")
            }, { throwable -> Timber.e(throwable, "Error retrieving place details") }
            )
        // TODO dispose this
        compositeDisposable.add(disposable)
    }
}

interface SearchLocationListener {

    fun onSearchLocationByAddressRequested(address: String)
}