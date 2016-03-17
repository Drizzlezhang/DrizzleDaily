package com.drizzle.drizzledaily.model;

import android.app.Application;
import android.content.Context;
import cn.bmob.v3.Bmob;

/**
 * Created by user on 2015/10/16.
 */
public class MyApplication extends Application {
	private static Context mContext;

	@Override public void onCreate() {
		super.onCreate();
		mContext = this;
		Bmob.initialize(this, "7674bc5c8d85e763b58e487eab5627a5");
	}

	public static Context getContext() {
		return mContext;
	}
}
