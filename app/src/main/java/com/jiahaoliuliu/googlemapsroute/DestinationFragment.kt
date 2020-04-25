package com.jiahaoliuliu.googlemapsroute

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.jiahaoliuliu.datalayer.DirectionRepository
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.entity.PlaceDetails
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
        private const val ARGUMENT_FINAL_LOCATION = "Argument final location"

        fun newInstance(finalLocation: Coordinate): DestinationFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARGUMENT_FINAL_LOCATION, finalLocation)
            val destinationFragment = DestinationFragment()
            destinationFragment.arguments = bundle
            return destinationFragment
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialLocation(DirectionRepository.BALI_AIRPORT_LOCATION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentDestinationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        MainApplication.getMainComponent()?.inject(this)
        arguments?.let {
            it.getParcelable<Coordinate>(ARGUMENT_FINAL_LOCATION)?.let {finalLocation ->
                setFinalLocation(finalLocation)
            }
        }
        binding.addressInput.setOnClickListener {
            onSearchLocationListener.onSearchLocationByAddressRequested(
                binding.addressInput.text.toString(), Caller.DESTINATION) }
        binding.showFullRouteButton.setOnClickListener{showFullRoute()}
        binding.voiceSearchIcon.setOnClickListener{ onSearchLocationListener.onSearchLocationByVoiceRequested(Caller.DESTINATION)}
        binding.pinLocationIcon.setOnClickListener{ onSearchLocationListener.onSearchLocationByPinRequested(Caller.DESTINATION)}
        super.onActivityCreated(savedInstanceState)
    }

    override fun onMapSynchronized() {
        showAirportLocation()
        if (hasFinalLocation()) {
            binding.showFullRouteButton.visibility = View.VISIBLE
        }
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
                drawMarker(placeDetails.location)
                drawRouteBetweenInitialAndFinalLocations(
                    DirectionRepository.BALI_AIRPORT_LOCATION, placeDetails.location
                )

                binding.showFullRouteButton.visibility = View.VISIBLE
            }, { throwable -> Timber.e(throwable, "Error retrieving place details") })
        compositeDisposable.add(disposable)
    }

    private fun showFullRoute() {
        directionRepository.initialLocation?.let { lastKnownLocationNotNull ->
            finalDestination?.let {finalDestinationNotNull ->
                drawRouteBetweenInitialAndFinalLocations(lastKnownLocationNotNull, DirectionRepository.DXB_AIRPORT_LOCATION,
                    boundMapToLocations = false, removePreviousRoute = false)

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