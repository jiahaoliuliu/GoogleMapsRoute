package com.jiahaoliuliu.networklayer.places

import com.google.gson.annotations.SerializedName
import com.jiahaoliuliu.networklayer.model.Bounds
import com.jiahaoliuliu.networklayer.model.Coordinate

data class PlaceDetailsNetworkResponse(
    // This field need to check more deeply
    @SerializedName("html_attributions") val htmlAttributions: List<String>,
    val result: Result, val status: String)

data class Result(@SerializedName("formatted_address") val formattedAddress: String,
    val name: String, val geometry: Geometry)

data class Geometry(val location: Coordinate, @SerializedName("viewport") val bounds: Bounds)