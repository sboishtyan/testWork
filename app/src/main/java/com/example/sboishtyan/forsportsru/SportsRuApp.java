package com.example.sboishtyan.forsportsru;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SportsRuApp extends Application {
    private BaseComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        setComponent(DaggerSportsRuAppComponent.builder().sportsRuAppModule(new SportsRuAppModule(this)).build());
    }

    @Override
    public Object getSystemService(String name) {
        if (Injector.matchesService(name)) {
            return component;
        }
        return super.getSystemService(name);
    }

    public BaseComponent getComponent() {
        return component;
    }

    public void setComponent(BaseComponent component) {
        this.component = component;
    }

}
