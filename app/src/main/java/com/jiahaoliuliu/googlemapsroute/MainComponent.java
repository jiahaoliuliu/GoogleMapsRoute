package com.jiahaoliuliu.googlemapsroute;

import com.jiahaoliuliu.networklayer.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ApplicationModule.class, NetworkModule.class})

@Singleton
public interface MainComponent {
}
