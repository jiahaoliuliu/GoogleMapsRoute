package com.jiahaoliuliu.googlemapsroute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.jiahaoliuliu.datalayer.GeocodeRepository
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentPingSearchBinding
import io.reactivex.disposables.CompositeDisposable
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentPingSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        MainApplication.getMainComponent()?.inject(this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onMapSynchronized() {
        googleMap?.let {
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
            val centerPosition = it.projection?.visibleRegion?.latLngBounds?.center
            Timber.v("Center position $centerPosition")
        }
    }

    override fun onMapCameraIdle() {
        val centerPosition = googleMap?.projection?.visibleRegion?.latLngBounds?.center
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}