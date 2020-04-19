package com.jiahaoliuliu.googlemapsroute

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
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
import com.jiahaoliuliu.datalayer.GeocodingRepository
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentDestinationBinding
import io.reactivex.android.schedulers.AndroidSchedulers
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

//    @Inject lateinit var distanceRepository: DistanceRepository
//    @Inject lateinit var directionRepository: DirectionRepository
    @Inject lateinit var geocodingRepository: GeocodingRepository
    private lateinit var binding: FragmentDestinationBinding
    private var googleMap: GoogleMap? = null
    private var addressToBeFound: String? = null
    private var userInputTimer: CountDownTimer? = null

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
        binding.addressInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                addressToBeFound = s.toString()
                userInputTimer?.cancel()
                userInputTimer = object: CountDownTimer(TIME_DIFFERENCE_FOR_INPUT, TIME_DIFFERENCE_FOR_INPUT) {
                    override fun onFinish() {
                        findAddressAndShowIt()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        // DO nothing
                    }
                }.start()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not do anything
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not do anything
            }
        })
    }

    private fun findAddressAndShowIt() {
        addressToBeFound?.let {
            geocodingRepository.retrieveLocation(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ location -> Timber.v("New location $location")
                }, {throwable -> Timber.e(throwable, "Error finding the address")})
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