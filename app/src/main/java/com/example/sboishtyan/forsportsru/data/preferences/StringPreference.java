package com.example.sboishtyan.forsportsru.data.preferences;

import android.content.SharedPreferences;

public class StringPreference extends BasePreference<String> {

    public StringPreference(SharedPreferences preferences, String key) {
        super(preferences, key);
    }

    @Override
    public String get() {
        return preferences.getString(key, defaultValue);
    }

    @Override
    public void set(String value) {
        preferences.edit().putString(key, value).apply();
    }
}
