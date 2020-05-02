package com.jiahaoliuliu.googlemapsroute

import com.jiahaoliuliu.entity.Coordinate

interface SearchLocationListener {

    fun onSearchLocationByAddressRequested(address: String, caller: Caller)

    fun onSearchLocationByPinRequested(caller: Caller, defaultLocation: Coordinate)

    fun onSearchLocationByVoiceRequested(caller: Caller)
}