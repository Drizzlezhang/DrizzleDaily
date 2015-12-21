package com.drizzle.drizzledaily.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.ResultCallback;
import com.zhy.http.okhttp.request.OkHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 引导页
 */

public class GildeActivity extends AppCompatActivity {

    @Bind(R.id.start_img)
    ImageView startImg;

    private static final String TESTURL="testurl";
    private static final String STARTIMGCACHEURL = "shartimgurl";

    /**
     * 开启一个handler请求图片并下载,记录图片url,如果重复则使用已有的
     */
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //TODO
                    if (NetUtils.isConnected(GildeActivity.this)) {
                        new OkHttpRequest.Builder().url(Config.START_IMAGE + "720*1184").get(new ResultCallback<String>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                TUtils.showShort(GildeActivity.this, "服务器出问题了");
                            }

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String imgurl = jsonObject.getString("img");
                                    Log.d("img", imgurl);
                                    SharedPreferences sharedPreferences = getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
                                    String cacheurl = sharedPreferences.getString(STARTIMGCACHEURL, "");
                                    if (cacheurl.equals(imgurl)) {
                                        Log.d("startimg", "exist");
                                    } else {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(STARTIMGCACHEURL, imgurl);
                                        editor.commit();
                                        //下载图片并覆盖
                                        new OkHttpRequest.Builder().url(imgurl).
                                                destFileDir(Config.START_PHOTO_FOLDER).
                                                destFileName("startimg.jpg").
                                                download(new ResultCallback<String>() {
                                                    @Override
                                                    public void onError(Request request, Exception e) {
                                                        Log.d("startimg", "failed");
                                                    }

                                                    @Override
                                                    public void onResponse(String o) {
                                                        Log.d("startimg", "succeed");
                                                    }
                                            });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    TUtils.showShort(GildeActivity.this, "json error");
                                }
                            }
                        });
                    } else {
                        TUtils.showShort(GildeActivity.this, "网络未连接");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gilde);
        ButterKnife.bind(this);
        File file = new File(Config.START_PHOTO_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        initDatabase();
        handler.sendEmptyMessageDelayed(1, 1000);
        playAnim();
    }

    /**
     * 为之前已存放到数据库的用户服务，将数据转为json存入sharedperence
     */
    private void initDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
        int collectversion = sharedPreferences.getInt(Config.COLLECTVERSION, 0);
        if (collectversion == 0) {
            CollectDB collectDB = CollectDB.getInstance(this);
            Set<CollectBean> collectBeanSet = collectDB.findSetCollects();
            Gson gson = new Gson();
            String collectCache = gson.toJson(collectBeanSet);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Config.COLLECTCACHE, collectCache);
            editor.putInt(Config.COLLECTVERSION, 1);
            editor.commit();
        } else {
            //TODO
        }
    }

    /**
     * 引导页图片动画效果
     */
    private void playAnim() {
        File file = new File(Config.START_PHOTO_FOLDER, "startimg.jpg");
        if (file.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            startImg.setImageBitmap(bitmap);
        } else {
            Glide.with(this).load(R.mipmap.start_img)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop().placeholder(R.mipmap.start_img)
                    .error(R.mipmap.start_img).crossFade()
                    .into(startImg);
        }
        ObjectAnimator alpha = ObjectAnimator.ofFloat(startImg, "alpha", 1f, 0.8f);
        ObjectAnimator scalex = ObjectAnimator.ofFloat(startImg, "scaleX", 1f, 1.1f);
        ObjectAnimator scaley = ObjectAnimator.ofFloat(startImg, "scaleY", 1f, 1.1f);
        AnimatorSet animator = new AnimatorSet();
        animator.play(alpha).with(scalex).with(scaley);
        animator.setDuration(2000).start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(GildeActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
