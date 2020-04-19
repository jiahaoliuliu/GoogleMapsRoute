package com.jiahaoliuliu.networklayer.places

import com.jiahaoliuliu.networklayer.GOOGLE_API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPIService {
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
    fun getLocation(@Query("language") language: String = LANGUAGE_EN,
                    @Query("address") address: String,
                    @Query("key") key: String = GOOGLE_API_KEY
    ): Single<String>

    /**
     * https://maps.googleapis.com/maps/api/place/autocomplete/json?
     *  input=Amoeba&
     *  language=en&
     *  key=YOUR_KEY
     */
    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json")
    fun getPredictions(@Query("language") language: String = LANGUAGE_EN,
                    @Query("input") input: String,
                    @Query("key") key: String = GOOGLE_API_KEY
    ): Single<String>
}