package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.networklayer.direction.GoogleDirectionAPIService
import com.jiahaoliuliu.networklayer.distance.GoogleDistanceMatrixAPIService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDistanceRepository(
        googleDistanceMatrixAPIService: GoogleDistanceMatrixAPIService
    ): DistanceRepository {
        return DistanceRepository(googleDistanceMatrixAPIService)
    }

    @Provides
    @Singleton
    fun provideDirectionRepository(
        googleDirectionAPIService: GoogleDirectionAPIService
    ): DirectionRepository {
        return DirectionRepository(googleDirectionAPIService)
    }
}