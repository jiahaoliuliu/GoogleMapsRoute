package com.jiahaoliuliu.networklayer

import com.jiahaoliuliu.networklayer.direction.GoogleDirectionAPIService
import com.jiahaoliuliu.networklayer.distance.GoogleDistanceMatrixAPIService
import com.jiahaoliuliu.networklayer.places.GooglePlacesAPIService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {
    companion object {
        private const val BASE_URL = "http://www.google.com"
    }

    private val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
    }

    @Provides
    fun provideGoogleDistanceMatrixApiService(): GoogleDistanceMatrixAPIService {
        return retrofit.create(GoogleDistanceMatrixAPIService::class.java)
    }

    @Provides
    fun provideGoogleDirectionsApiService(): GoogleDirectionAPIService {
        return retrofit.create(GoogleDirectionAPIService::class.java)
    }

    @Provides
    fun provideGooglePlacesApiService(): GooglePlacesAPIService {
        return retrofit.create(GooglePlacesAPIService::class.java)
    }
}