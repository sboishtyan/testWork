package com.example.sboishtyan.forsportsru.api;

import android.util.Log;

import rx.Subscriber;

public class BaseSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public  void onError(Throwable e) {
        Log.e(this.getClass().getSimpleName(), e.getMessage());
    }

    @Override
    public void onNext(T t) {

    }
}
