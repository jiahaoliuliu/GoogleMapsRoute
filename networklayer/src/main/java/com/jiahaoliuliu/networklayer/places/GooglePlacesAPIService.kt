package com.jiahaoliuliu.networklayer.places

import com.jiahaoliuliu.networklayer.GOOGLE_API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPIService {
    companion object {
        private const val LANGUAGE_EN = "en"
        private const val FIELDS = "formatted_address,name,geometry,place_id"
    }

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
    ): Single<AutoCompleteNetworkResponse>

    /**
     * https://maps.googleapis.com/maps/api/place/details/json?
     * placeid=ChIJ43gpdqJdXz4RZ_uEtyY5dHk&
     * key=YOUR_KEY&
     * Language=en&
     * fields=formatted_address,name,geometry,place_id
     */
    @GET("https://maps.googleapis.com/maps/api/place/details/json")
    fun getPlaceDetails(@Query("language") language: String = LANGUAGE_EN,
                        @Query("placeid") placeId: String,
                        @Query("key") key: String = GOOGLE_API_KEY,
                        @Query("fields") fields: String = FIELDS
    ): Single<PlaceDetailsNetworkResponse>
}