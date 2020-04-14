package com.jiahaoliuliu.googlemapsroute

import android.app.Application
import timber.log.Timber

class MainApplication: Application() {

    private var mainComponent: MainComponent? = null

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        mainComponent = DaggerMainComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    fun getMainComponent() = mainComponent
}