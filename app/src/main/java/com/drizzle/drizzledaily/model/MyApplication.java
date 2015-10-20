package com.drizzle.drizzledaily.model;

import android.app.Application;

import cn.bmob.v3.Bmob;
import im.fir.sdk.FIR;

/**
 * Created by user on 2015/10/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
        Bmob.initialize(this, "7674bc5c8d85e763b58e487eab5627a5");
    }
}
