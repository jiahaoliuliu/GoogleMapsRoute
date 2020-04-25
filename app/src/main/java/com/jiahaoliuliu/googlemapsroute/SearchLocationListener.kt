package com.jiahaoliuliu.googlemapsroute

interface SearchLocationListener {

    fun onSearchLocationByAddressRequested(address: String, caller: Caller)

    fun onSearchLocationByPinRequested(caller: Caller)

    fun onSearchLocationByVoiceRequested(caller: Caller)
}