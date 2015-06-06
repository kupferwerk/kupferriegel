package com.kupferwerk.kupferriegel.core;

import android.app.Application;

import com.kupferwerk.kupferriegel.R;

import io.relayr.RelayrSdk;

public class KupferRiegelApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean isMockMode = getResources().getBoolean(R.bool.isMockMode);
        new RelayrSdk.Builder(this).inMockMode(isMockMode).build();
    }
}
