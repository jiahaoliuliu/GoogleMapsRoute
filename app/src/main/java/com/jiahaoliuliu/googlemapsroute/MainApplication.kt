package com.jiahaoliuliu.googlemapsroute

import android.app.Application
import timber.log.Timber

class MainApplication: Application() {


    companion object {
        private var mainComponent: MainComponent? = null
        fun getMainComponent() = mainComponent
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        mainComponent = DaggerMainComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

}