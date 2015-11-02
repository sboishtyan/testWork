package com.example.sboishtyan.forsportsru.data.preferences;

import android.content.SharedPreferences;

public abstract class BasePreference<T> {
    protected final SharedPreferences preferences;
    protected final String key;
    protected final T defaultValue;

    public BasePreference(SharedPreferences preferences, String key) {
        this(preferences, key, null);
    }

    public BasePreference(SharedPreferences preferences, String key, T defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public abstract T get();
    public abstract void set(T value);
    public boolean isSet() { return preferences.contains(key);}
    public void delete() { preferences.edit().remove(key); }
}
