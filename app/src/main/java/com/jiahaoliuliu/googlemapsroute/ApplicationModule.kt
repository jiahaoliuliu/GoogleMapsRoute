package com.jiahaoliuliu.googlemapsroute

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val context: Context) {
    @Provides
    fun provideContext(): Context {
        return context
    }
}