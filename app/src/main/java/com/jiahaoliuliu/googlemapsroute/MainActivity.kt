package com.jiahaoliuliu.googlemapsroute

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.jiahaoliuliu.googlemapsroute.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // TODO: Pass the arguments
    private val originFragment = OriginFragment()
    private val destinationFragment = OriginFragment()

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
                    showOriginTab()
                } else {
                    showDestinationTab()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        showOriginTab()
    }

    private fun showOriginTab() {
        supportFragmentManager.beginTransaction().replace(R.id.container, originFragment).commit()
    }

    private fun showDestinationTab() {
        supportFragmentManager.beginTransaction().replace(R.id.container, destinationFragment).commit()
    }
}
