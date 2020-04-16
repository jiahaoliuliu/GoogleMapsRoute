package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.networklayer.GoogleDistanceMatrixAPIService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDistanceRepository(
        googleDistanceMatrixAPIService: GoogleDistanceMatrixAPIService): DistanceRepository {
        return DistanceRepository(googleDistanceMatrixAPIService)
    }
}