package com.jiahaoliuliu.networklayer.direction

import com.jiahaoliuliu.networklayer.GOOGLE_API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionAPIService {

    companion object {
        private const val LANGUAGE_EN = "en"
        private const val UNITS_METRIC = "metric"
    }

    /**
     * https://maps.googleapis.com/maps/api/directions/json?
     *      origin=25.276,55.296&
     *      destination=25.2527777777778,55.3644444444444&key=AIzaSyD8fbannDNMQRswwN5h1jsGQXWyvRvGKr4&
     *      language=en&
     *      units=metric
     */
    @GET("https://maps.googleapis.com/maps/api/directions/json")
    fun getDistance(@Query("language") language: String = LANGUAGE_EN, @Query("units") units: String = UNITS_METRIC,
                    @Query("origin", encoded=true) origin: String, @Query("destination", encoded=true) destinations: String,
                    @Query("key") key: String = GOOGLE_API_KEY
    ): Single<String>

}