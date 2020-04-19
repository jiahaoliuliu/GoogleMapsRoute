package com.jiahaoliuliu.networklayer.geocoding

import com.jiahaoliuliu.networklayer.GOOGLE_API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleGeocodingAPIService {
    companion object {
        private const val LANGUAGE_EN = "en"
    }

    /**
     * https://maps.googleapis.com/maps/api/geocode/json?
     * address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&
     * key=YOUR_API_KEY
     */
    @GET("https://maps.googleapis.com/maps/api/geocode/json")
    fun getLocation(@Query("language") language: String = LANGUAGE_EN,
                    @Query("address") address: String,
                    @Query("key") key: String = GOOGLE_API_KEY
    ): Single<String>
}