package com.example.sboishtyan.forsportsru;

import com.example.sboishtyan.forsportsru.ui.NewsActivity;
import com.example.sboishtyan.forsportsru.ui.StartActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(modules = SportsRuAppModule.class)
public interface SportsRuAppComponent extends BaseComponent {
    void inject(StartActivity startActivity);
    void inject(NewsActivity newsActivity);
}
