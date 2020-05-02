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
import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.entity.Direction
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
        private const val ARGUMENT_FINAL_ID = "Argument final id"

        fun newInstance(finalLocation: Coordinate): DestinationFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARGUMENT_FINAL_LOCATION, finalLocation)
            val destinationFragment = DestinationFragment()
            destinationFragment.arguments = bundle
            return destinationFragment
        }

        fun newInstance(placeId: String): DestinationFragment {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_FINAL_ID, placeId)
            val destinationFragment = DestinationFragment()
            destinationFragment.arguments = bundle
            return destinationFragment
        }
    }

    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentDestinationBinding
    private lateinit var onSearchLocationListener: SearchLocationListener
    private var finalPlaceId: String? = null

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

            if (it.containsKey(ARGUMENT_FINAL_ID)) {
                finalPlaceId = it.getString(ARGUMENT_FINAL_ID)
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

        finalPlaceId?.let {
            val disposable = placesRepository.retrievePlaceDetails(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ placeDetails ->
                    binding.addressInput.text = placeDetails.name
                    setFinalLocation(placeDetails.location)

                    // Show the full route button if the initial location was set
                    directionRepository.initialLocation?.let {
                        binding.showFullRouteButton.visibility = View.VISIBLE
                    }
                }, { throwable -> Timber.e(throwable, "Error retrieving place details") })
            compositeDisposable.add(disposable)
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

    private fun showFullRoute() {
        directionRepository.initialLocation?.let { initialLocationNotNull ->
            getFinalLocation()?.let { finalLocationNotNull ->
                drawRouteBetweenInitialAndFinalLocations(initialLocationNotNull, DirectionRepository.DXB_AIRPORT_LOCATION,
                    boundMapToLocations = false, removePreviousRoute = false, showRoute = false)

                // Draw a line between the Dubai airport and Bali airport
                googleMap?.addPolyline(
                    PolylineOptions()
                        .color(ContextCompat.getColor(context!!, R.color.colorRoute))
                        .add(DirectionRepository.DXB_AIRPORT_LOCATION.toLatLng(), DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng()))

                boundMapToLocations(initialLocationNotNull.toLatLng(), DirectionRepository.DXB_AIRPORT_LOCATION.toLatLng(),
                    DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng(), finalLocationNotNull.toLatLng())

                // Show time
                addMarkerBetweenLocations("15h 40mins", arrayListOf(DirectionRepository.DXB_AIRPORT_LOCATION.toLatLng(), DirectionRepository.BALI_AIRPORT_LOCATION.toLatLng()))

                // Hide de routes. This is not done yet
                binding.bottomSheet.root.visibility = View.GONE
            }
        }
    }

    override fun showProgressScreen(showIt: Boolean) {
        val visibility = if (showIt) View.VISIBLE else View.GONE
        binding.progressBar.visibility = visibility
    }

    override fun onNewRouteDrawn(direction: Direction, showRoute: Boolean) {
        // Set the steps list
        val visibility = if (showRoute) View.VISIBLE else View.GONE
        binding.bottomSheet.root.visibility = visibility

        binding.bottomSheet.direction = direction
        val stepsListAdapter = StepsListAdapter()
        stepsListAdapter.updateStepsList(direction.stepsList)
        binding.bottomSheet.stepsList.adapter = stepsListAdapter
    }

    override fun setFinalLocation(finalLocation: Coordinate) {
        drawMarker(finalLocation)
        super.setFinalLocation(finalLocation)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}