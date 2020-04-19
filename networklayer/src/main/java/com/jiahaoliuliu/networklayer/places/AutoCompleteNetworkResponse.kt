package com.jiahaoliuliu.networklayer.places

import com.google.gson.annotations.SerializedName

data class AutoCompleteNetworkResponse(val predictions: List<Prediction>, val status: String)

data class Prediction(val description: String, @SerializedName("place_id") val placeId: String,
                      @SerializedName("structured_formatting") val structuredFormatting: StructuredFormatting,
                      val types: List<String>
    )

data class StructuredFormatting(
    @SerializedName("main_text") val mainText: String,
    @SerializedName("secondary_text") val secondaryText: String)