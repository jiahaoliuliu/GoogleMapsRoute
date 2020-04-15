package com.jiahaoliuliu.networklayer

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDistanceAPIService {
    companion object {
        private const val LANGUAGE_EN = "en"
        private const val UNITS_METRIC = "metric"
    }

    /**
     * https://maps.googleapis.com/maps/api/distancematrix/json?
     * language=en&
     * units=metric&
     * origins=25.2113571%2C+55.27616868&
     * destinations=25.2527777777778%2C+55.3644444444444%7C23.8803729%2C+55.273573%7C24.47063%2C+54.37723&
     * key=AIzaSyBfVbxWNBHbb7nf-9ceoGmjkIjBLp4bDI0
     */
    @GET("https://maps.googleapis.com/maps/api/distancematrix/json")
    fun getDistance(@Query("language") language: String = LANGUAGE_EN, @Query("units") units: String = UNITS_METRIC,
        @Query("origins", encoded=true) origin: String, @Query("destinations", encoded=true) destinations: String,
        @Query("key") key: String = GOOGLE_API_KEY): Single<DistanceNetworkResponse>
}