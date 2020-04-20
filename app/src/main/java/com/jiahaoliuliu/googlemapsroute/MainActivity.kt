package com.jiahaoliuliu.googlemapsroute

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.jiahaoliuliu.googlemapsroute.LocationSearchFragment.Caller
import com.jiahaoliuliu.googlemapsroute.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SearchLocationListener, OnLocationFoundListener {

    private lateinit var binding: ActivityMainBinding
    private val originFragment = OriginFragment()
    private val destinationFragment = DestinationFragment()
    private var locationSearchFragment: LocationSearchFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        setUpTabs()
    }

    private fun setUpTabs() {
        val originTab = binding.locationTabs.newTab().setText("To Dubai airport")
        binding.locationTabs.addTab(originTab)
        val destinationTab = binding.locationTabs.newTab().setText("Final destination")
        binding.locationTabs.addTab(destinationTab)
        binding.locationTabs.tabGravity = TabLayout.GRAVITY_FILL
        binding.locationTabs.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab == originTab) {
                    showOriginScreen()
                } else {
                    showDestinationScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        showOriginScreen()
    }

    private fun showOriginScreen() {
        supportFragmentManager.beginTransaction().replace(R.id.container, originFragment).commit()
    }

    private fun showDestinationScreen() {
        supportFragmentManager.beginTransaction().replace(R.id.container, destinationFragment).commit()
    }

    override fun onSearchLocationByAddressRequested(address: String, caller: Caller) {
        locationSearchFragment = LocationSearchFragment.newInstance(address, caller)
        supportFragmentManager.beginTransaction().replace(R.id.container, locationSearchFragment!!).commit()
    }

    override fun onLocationFound(id: String, caller: Caller) {
        when (caller) {
            Caller.ORIGIN -> {
                originFragment.showRouteFromLocation(id)
                showOriginScreen()
            }
            Caller.DESTINATION -> {
                destinationFragment.showRouteToLocation(id)
                showDestinationScreen()
            }
        }
    }
}