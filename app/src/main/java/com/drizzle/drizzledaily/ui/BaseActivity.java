package com.drizzle.drizzledaily.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.ThemeUtils;

/**
 * 解决主题切换问题
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
        int themeid = preferences.getInt(Config.SKIN_NUMBER, 2);
        ThemeUtils.onActivityCreateSetTheme(this, themeid);
        super.onCreate(savedInstanceState);
    }
}
