package com.drizzle.drizzledaily.model;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import im.fir.sdk.FIR;

/**
 * Created by user on 2015/10/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient client = com.zhy.http.okhttp.OkHttpClientManager.getInstance().getOkHttpClient();
        client.setConnectTimeout(100000, TimeUnit.MILLISECONDS);
        FIR.init(this);
        Bmob.initialize(this, "7674bc5c8d85e763b58e487eab5627a5");
    }
}
