package com.jiahaoliuliu.googlemapsroute;

import com.jiahaoliuliu.datalayer.RepositoryModule;
import com.jiahaoliuliu.networklayer.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ApplicationModule.class, RepositoryModule.class, NetworkModule.class})

@Singleton
public interface MainComponent {
    void inject(OriginFragment originFragment);
}
