package com.example.sboishtyan.forsportsru.data;

import android.content.SharedPreferences;

import com.example.sboishtyan.forsportsru.CommonModule;
import com.example.sboishtyan.forsportsru.data.preferences.StringPreference;
import com.example.sboishtyan.forsportsru.data.qualifiers.TeamId;
import com.example.sboishtyan.forsportsru.data.qualifiers.TeamTitle;
import com.example.sboishtyan.forsportsru.util.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = { CommonModule.class })
public class DataModule {
    @Provides @Singleton @TeamId
    StringPreference provideTeamId(SharedPreferences preferences) {
        return new StringPreference(preferences, Constants.TEAM_ID);
    }
    @Provides @Singleton @TeamTitle
    StringPreference provideTeamTitle(SharedPreferences preferences) {
        return new StringPreference(preferences, Constants.TEAM_TILE);
    }
}
