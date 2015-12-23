package com.drizzle.drizzledaily.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
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

	@Bind(R.id.start_img) ImageView startImg;

	private static final String STARTIMGCACHEURL = "shartimgurl";

	/**
	 * 开启一个handler请求图片并下载,记录图片url,如果重复则使用已有的
	 */
	private android.os.Handler handler = new android.os.Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					//TODO
					if (NetUtils.isConnected(GildeActivity.this)) {
						OkHttpUtils.get().url(Config.START_IMAGE + "720*1184").build().execute(new StringCallback() {
							@Override public void onError(Request request, Exception e) {
								TUtils.showShort(GildeActivity.this, "服务器出问题了");
							}

							@Override public void onResponse(String response) {
								try {
									JSONObject jsonObject = new JSONObject(response);
									String imgurl = jsonObject.getString("img");
									Log.d("img", imgurl);
									String cacheurl = PerferUtils.getString(STARTIMGCACHEURL);
									if (cacheurl.equals(imgurl)) {
										Log.d("startimg", "exist");
									} else {
										PerferUtils.saveSth(STARTIMGCACHEURL, imgurl);
										//下载图片并覆盖
										OkHttpUtils.get().url(imgurl).build()
											.execute(new FileCallBack(Config.START_PHOTO_FOLDER, "startimg.jpg")//
												{
													@Override public void inProgress(float progress) {
														Log.d("progress", progress + "");
													}

													@Override public void onError(Request request, Exception e) {
														Log.d("startimg", "failed");
													}

													@Override public void onResponse(File file) {
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

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//顶部状态栏和底部导航栏透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
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
		int collectversion = PerferUtils.getInt(Config.COLLECTVERSION);
		if (collectversion == 0) {
			CollectDB collectDB = CollectDB.getInstance(this);
			Set<CollectBean> collectBeanSet = collectDB.findSetCollects();
			Gson gson = new Gson();
			String collectCache = gson.toJson(collectBeanSet);
			PerferUtils.saveSth(Config.COLLECTCACHE, collectCache);
			PerferUtils.saveSth(Config.COLLECTVERSION, 1);
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
			Glide.with(this)
				.load(R.mipmap.start_img)
				.diskCacheStrategy(DiskCacheStrategy.NONE)
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
		animator.setDuration(2000).start();
		animator.addListener(new AnimatorListenerAdapter() {
			@Override public void onAnimationEnd(Animator animation) {
				startActivity(new Intent(GildeActivity.this, MainActivity.class));
				finish();
			}
		});
	}
}
