package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.networklayer.GoogleDistanceAPIService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDistanceRepository(
        googleDistanceAPIService: GoogleDistanceAPIService): DistanceRepository {
        return DistanceRepository(googleDistanceAPIService)
    }
}