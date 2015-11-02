package com.example.sboishtyan.forsportsru;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module
public class CommonModule {
    @Provides @Singleton
    SharedPreferences provideSharedPreferences (SportsRuApp app) {
        return app.getSharedPreferences("sportsRuApp", MODE_PRIVATE);
    }
}
