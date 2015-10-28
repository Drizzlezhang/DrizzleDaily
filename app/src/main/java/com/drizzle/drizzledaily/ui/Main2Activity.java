package com.drizzle.drizzledaily.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Set<CollectBean> collectBeanSet=new HashSet<>();
        Gson gson=new Gson();
        String s=gson.toJson(collectBeanSet);
        Log.d("json",s);
    }
}
