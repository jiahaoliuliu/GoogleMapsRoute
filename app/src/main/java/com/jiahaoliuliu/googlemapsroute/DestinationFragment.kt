package com.jiahaoliuliu.googlemapsroute

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.jiahaoliuliu.datalayer.DirectionRepository
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.entity.PlaceDetails
import com.jiahaoliuliu.googlemapsroute.LocationSearchFragment.Caller
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentDestinationBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DestinationFragment: AbsBaseMapFragment() {

    companion object {
        private const val DEFAULT_ZOOM = 15F
        private val compositeDisposable = CompositeDisposable()
    }

    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentDestinationBinding
    private lateinit var onSearchLocationListener: SearchLocationListener
    private var finalDestination: PlaceDetails? = null

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
            onSearchLocationListener.onSearchLocationByAddressRequested(
                binding.addressInput.text.toString(), Caller.DESTINATION) }
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
            markerOptions.position(DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng())
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            googleMapNotNull.addMarker(markerOptions)

            googleMapNotNull.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng(), DEFAULT_ZOOM))
        }
    }

    fun showRouteToLocation(placeId: String) {
        val disposable = placesRepository.retrievePlaceDetails(placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ placeDetails ->
                finalDestination = placeDetails
                binding.addressInput.text = placeDetails.name
                drawRouteBetweenOriginAndDestination(
                    DirectionRepository.BALI_AIRPORT_LOCATION, placeDetails.location
                )

                binding.showFullRouteButton.visibility = View.VISIBLE
            }, { throwable -> Timber.e(throwable, "Error retrieving place details") })
        compositeDisposable.add(disposable)
    }

    private fun showFullRoute() {
        directionRepository.initialLocation?.let { lastKnownLocationNotNull ->
            finalDestination?.let {finalDestinationNotNull ->
                drawRouteBetweenOriginAndDestination(lastKnownLocationNotNull, DirectionRepository.DXB_AIRPORT_LOCATION, boundMapToLocations = false)

                // Draw a line between the Dubai airport and Bali airport
                googleMap?.addPolyline(
                    PolylineOptions()
                        .color(ContextCompat.getColor(context!!, R.color.colorRoute))
                        .add(DirectionRepository.DXB_AIRPORT_LOCATION.toLatLng(), DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng()))

                boundMapToLocations(lastKnownLocationNotNull.toLatLng(), DirectionRepository.DXB_AIRPORT_LOCATION.toLatLng(),
                    DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng(), finalDestinationNotNull.location.toLatLng())

                // Show time
                addMarkerBetweenLocations("15h 40mins", arrayListOf(DirectionRepository.DXB_AIRPORT_LOCATION.toLatLng(), DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng()))
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
}

interface SearchLocationListener {

    fun onSearchLocationByAddressRequested(address: String, caller: Caller)

    fun onSearchLocationByPinRequested()
}