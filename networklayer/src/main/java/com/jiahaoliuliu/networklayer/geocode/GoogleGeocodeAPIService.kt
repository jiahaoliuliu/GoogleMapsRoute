package com.jiahaoliuliu.networklayer.geocode

import com.jiahaoliuliu.networklayer.GOOGLE_API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleGeocodeAPIService {
    companion object {
        private const val LANGUAGE_EN = "en"
    }

    /**
     * https://maps.googleapis.com/maps/api/place/autocomplete/json?
     * language=en&
     * input=Amoeba&
     * key=YOUR_KEY
     */
    @GET("https://maps.googleapis.com/maps/api/geocode/json")
    fun getLocationBasedOnAddress(@Query("language") language: String = LANGUAGE_EN,
                    @Query("address") address: String,
                    @Query("key") key: String = GOOGLE_API_KEY
    ): Single<String>

    /**
     * https://maps.googleapis.com/maps/api/geocode/json?
     *  language=en&
     *  latlng=40.714224,-73.961452&
     *  key=YOUR_KEY
     */
    @GET("https://maps.googleapis.com/maps/api/geocode/json")
    fun getAddressBasedOnLocation(@Query("language") language: String = LANGUAGE_EN,
                               @Query("latlng") location: String,
                               @Query("key") key: String = GOOGLE_API_KEY
    ): Single<String>
}