package com.example.sboishtyan.forsportsru;

import com.example.sboishtyan.forsportsru.data.DataModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DataModule.class, ApiModule.class})
public final class SportsRuAppModule {
    private SportsRuApp app;

    public SportsRuAppModule(SportsRuApp app) {
        this.app = app;
    }

    @Provides @Singleton
    SportsRuApp provideApplication(){return app;}
}
