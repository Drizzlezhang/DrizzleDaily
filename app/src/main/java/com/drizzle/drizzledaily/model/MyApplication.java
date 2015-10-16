package com.drizzle.drizzledaily.model;

import android.app.Application;

import im.fir.sdk.FIR;

/**
 * Created by user on 2015/10/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        FIR.init(this);
        super.onCreate();
    }
}
