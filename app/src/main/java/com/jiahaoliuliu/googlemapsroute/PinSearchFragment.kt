package com.jiahaoliuliu.googlemapsroute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.jiahaoliuliu.datalayer.GeocodeRepository
import com.jiahaoliuliu.entity.Address
import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentPingSearchBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class PinSearchFragment: AbsBaseMapFragment() {

    companion object {
        private val DEFAULT_LOCATION = LatLng(25.276, 55.296)
        private const val DEFAULT_ZOOM = 15F
        private val compositeDisposable = CompositeDisposable()
    }

    @Inject lateinit var geocodeRepository: GeocodeRepository
    private lateinit var binding: FragmentPingSearchBinding
    private var finalPosition: Coordinate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentPingSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        MainApplication.getMainComponent()?.inject(this)
        binding.setLocationButton.setOnClickListener {
            setLocation()
        }
        super.onActivityCreated(savedInstanceState)
    }

    private fun setLocation() {
        finalPosition?.let {
            // Return to the activity with the position
        }
    }

    override fun onMapSynchronized() {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
    }

    override fun onMapCameraIdle() {
        val centerPosition = googleMap?.projection?.visibleRegion?.latLngBounds?.center
        centerPosition?.let {
            finalPosition = it.toCoordinate()
            geocodeRepository.retrieveAddress(centerPosition.toCoordinate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({address ->
                    showAddress(address)
                }, {throwable ->
                    Timber.e(throwable, "Error getting the address")
                })
        }
    }

    private fun showAddress(address: Address) {
        binding.addressFound.text = address.name
        if (binding.addressFound.visibility == View.GONE) {
            binding.addressFound.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}