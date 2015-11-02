package com.example.sboishtyan.forsportsru;

import android.content.Context;

public final class Injector {
    private final static String INJECTOR_SERVICE = "com.example.sboishtyan.forsportsru.injector";

    public static BaseComponent obtain(Context context){
        return (BaseComponent) context.getSystemService(INJECTOR_SERVICE);
    }

    public static boolean matchesService(final String name){
        return INJECTOR_SERVICE.equals(name);
    }

    private Injector(){}
}
