package com.jiahaoliuliu.networklayer.geocode

import com.google.gson.annotations.SerializedName

data class GeocodeNetworkResponse(
        val results: List<Result>,
        val status: String)

data class Result(@SerializedName("formatted_address") val formattedAddress: String,
                  @SerializedName("place_id") val placeId: String)