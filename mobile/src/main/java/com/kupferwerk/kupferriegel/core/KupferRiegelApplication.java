package com.kupferwerk.kupferriegel.core;

import android.app.Application;

import io.relayr.RelayrSdk;

public class KupferRiegelApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new RelayrSdk.Builder(this).build();
    }
}
