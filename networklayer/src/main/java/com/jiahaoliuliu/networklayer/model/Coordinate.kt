package com.jiahaoliuliu.networklayer.model

import com.google.gson.annotations.SerializedName

data class Coordinate(
    @SerializedName("lat")val latitude: Double,
    @SerializedName("lng")val longitude: Double)