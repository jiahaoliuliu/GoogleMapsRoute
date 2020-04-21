package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.networklayer.direction.GoogleDirectionAPIService
import com.jiahaoliuliu.networklayer.distance.GoogleDistanceMatrixAPIService
import com.jiahaoliuliu.networklayer.geocode.GoogleGeocodeAPIService
import com.jiahaoliuliu.networklayer.places.GooglePlacesAPIService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDistanceRepository(
        googleDistanceMatrixAPIService: GoogleDistanceMatrixAPIService
    )= DistanceRepository(googleDistanceMatrixAPIService)

    @Provides
    @Singleton
    fun provideDirectionRepository(
        googleDirectionAPIService: GoogleDirectionAPIService
    ) = DirectionRepository(googleDirectionAPIService)

    @Provides
    @Singleton
    fun providePlacesRepository(googlePlacesAPIService: GooglePlacesAPIService) =
        PlacesRepository(googlePlacesAPIService)

    @Provides
    @Singleton
    fun provideGeocodeRepository(googleGeocodeAPIService: GoogleGeocodeAPIService) =
        GeocodeRepository(googleGeocodeAPIService)
}