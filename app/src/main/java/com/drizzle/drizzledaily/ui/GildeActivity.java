package com.drizzle.drizzledaily.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 引导页
 */

public class GildeActivity extends AppCompatActivity {
    @Bind(R.id.start_img)
    ImageView startImg;
    /**
     * 开启一个handler请求图片并下载
     */
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //TODO
                    OkHttpClientManager.getAsyn(Config.START_IMAGE + "480*728", new OkHttpClientManager.StringCallback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            TUtils.showShort(GildeActivity.this, "服务器出问题了");
                        }

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String imgurl = jsonObject.getString("img");
                                OkHttpClientManager.downloadAsyn(imgurl, Config.START_PHOTO_FOLDER,
                                        "startimg.jpg", new OkHttpClientManager.StringCallback() {
                                            @Override
                                            public void onFailure(Request request, IOException e) {
                                                Log.d("startimg", "failed");
                                            }

                                            @Override
                                            public void onResponse(String response) {
                                                Log.d("startimg", "succeed");
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                TUtils.showShort(GildeActivity.this, "json问题");
                            }

                        }
                    });
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
        File file=new File(Config.START_PHOTO_FOLDER);
        if (!file.exists()){
            file.mkdirs();
        }
        handler.sendEmptyMessageDelayed(1, 2000);
        playAnim();
    }

    /**
     * 引导页图片动画效果
     */
    private void playAnim() {
        File file = new File(Config.START_PHOTO_FOLDER, "startimg.jpg");
        if (file.exists()) {
            Glide.with(this)
                    .load(file)
                    .centerCrop()
                    .placeholder(R.mipmap.start_img)
                    .error(R.mipmap.start_img)
                    .crossFade()
                    .into(startImg);
        }
        ObjectAnimator alpha = ObjectAnimator.ofFloat(startImg, "alpha", 1f, 0.8f);
        ObjectAnimator scalex = ObjectAnimator.ofFloat(startImg, "scaleX", 1f, 1.1f);
        ObjectAnimator scaley = ObjectAnimator.ofFloat(startImg, "scaleY", 1f, 1.1f);
        AnimatorSet animator = new AnimatorSet();
        animator.play(alpha).with(scalex).with(scaley);
        animator.setDuration(2500).start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(GildeActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.not_move);
            }
        });
    }
}
