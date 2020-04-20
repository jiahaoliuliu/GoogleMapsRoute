package com.jiahaoliuliu.googlemapsroute

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentDestinationBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DestinationFragment: AbsBaseMapFragment() {

    companion object {
        private val BALI_AIRPORT_LOCATION = LatLng(-8.744000, 115.174858)
        private const val DEFAULT_ZOOM = 15F
    }

    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentDestinationBinding
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
        MainApplication.getMainComponent()?.inject(this)
        binding.addressInput.setOnClickListener {
            onSearchLocationListener.onSearchLocationByAddressRequested(binding.addressInput.text.toString()) }
        binding.showFullRouteButton.setOnClickListener{showFullRoute()}
        super.onActivityCreated(savedInstanceState)
    }

    override fun onMapSynchronized() {
        showAirportLocation()
        binding.addressInput.visibility = View.VISIBLE
    }

    private fun showAirportLocation() {
        googleMap?.let {googleMapNotNull ->
            val markerOptions = MarkerOptions()
            markerOptions.position(BALI_AIRPORT_LOCATION)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            googleMapNotNull.addMarker(markerOptions)

            googleMapNotNull.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    BALI_AIRPORT_LOCATION, DEFAULT_ZOOM))
        }
    }

    fun showRouteToLocation(placeId: String) {
        val disposable = placesRepository.retrievePlaceDetails(placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ placeDetails ->
                binding.addressInput.text = placeDetails.name
                drawRouteBetweenOriginAndDestination(
                    BALI_AIRPORT_LOCATION.toCoordinate(), placeDetails.location
                )

                binding.showFullRouteButton.visibility = View.VISIBLE
            }, { throwable -> Timber.e(throwable, "Error retrieving place details") })
        compositeDisposable.add(disposable)
    }

    private fun showFullRoute() {

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}

interface SearchLocationListener {

    fun onSearchLocationByAddressRequested(address: String)
}