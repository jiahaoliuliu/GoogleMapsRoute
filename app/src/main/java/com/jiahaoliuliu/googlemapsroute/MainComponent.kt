package com.jiahaoliuliu.googlemapsroute

import com.jiahaoliuliu.datalayer.RepositoryModule
import com.jiahaoliuliu.networklayer.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class, RepositoryModule::class, NetworkModule::class])
@Singleton
interface MainComponent {
    fun inject(originFragment: OriginFragment?)
    fun inject(destinationFragment: DestinationFragment?)
    fun inject(locationSearchFragment: LocationSearchFragment?)
}