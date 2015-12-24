package com.drizzle.drizzledaily.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.google.gson.Gson;
import com.hanks.htextview.HTextView;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 引导页
 */

public class GildeActivity extends AppCompatActivity {

	@Bind(R.id.start_img) ImageView startImg;

	@Bind(R.id.htext) HTextView hText;

	private static final String STARTIMGCACHEURL = "shartimgurl";

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//顶部状态栏和底部导航栏透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.activity_gilde);
		ButterKnife.bind(this);
		handler.sendEmptyMessage(1);
		playAnim();
	}

	/**
	 * 开启一个handler请求图片并下载,记录图片url,如果重复则使用已有的
	 */
	private Handler handler = new android.os.Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					if (NetUtils.isConnected(GildeActivity.this)) {
						updateStartImg();
					} else {
						TUtils.showShort(GildeActivity.this, "网络未连接");
					}
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 更新首页图片,RxJava实验
	 */
	private void updateStartImg() {
		OkHttpUtils.get().url(Config.START_IMAGE + "720*1184").build().execute(new StringCallback() {
			@Override public void onError(Request request, Exception e) {
				TUtils.showShort(GildeActivity.this, "服务器出问题了");
			}

			@Override public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					final String imgurl = jsonObject.getString("img");
					final String cacheurl = PerferUtils.getString(STARTIMGCACHEURL);
					Observable.just(imgurl).filter(new Func1<String, Boolean>() {
						@Override public Boolean call(String s) {
							return !s.equals(cacheurl);
						}
					}).map(new Func1<String, String>() {
						@Override public String call(String s) {
							PerferUtils.saveSth(STARTIMGCACHEURL, imgurl);
							return s;
						}
					}).map(new Func1<String, Void>() {
						@Override public Void call(String s) {
							//下载图片并覆盖
							OkHttpUtils.get()
								.url(s)
								.build()
								.execute(new FileCallBack(Config.START_PHOTO_FOLDER, "startimg.jpg") {
									@Override public void inProgress(float progress) {
										Log.d("progress", progress + "");
									}

									@Override public void onError(Request request, Exception e) {
										//	Log.d("startimg", "failed");
									}

									@Override public void onResponse(File file) {
										//	Log.d("startimg", "succeed");
									}
								});
							return null;
						}
					}).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Void>() {
						@Override public void onCompleted() {
							Log.d("startimg", "succeed");
						}

						@Override public void onError(Throwable e) {
							Log.d("startimg", "failed");
						}

						@Override public void onNext(Void aBoolean) {

						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
					TUtils.showShort(GildeActivity.this, "json error");
				}
			}
		});
	}

	/**
	 * 引导页图片动画效果
	 */
	private void playAnim() {
		File filefolder = new File(Config.START_PHOTO_FOLDER);
		if (!filefolder.exists()) {
			filefolder.mkdirs();
		}
		final File file = new File(Config.START_PHOTO_FOLDER, "startimg.jpg");
		Observable.just(file).map(new Func1<File, Boolean>() {
			@Override public Boolean call(File file) {
				return file.exists();
			}
		}).map(new Func1<Boolean, Void>() {
			@Override public Void call(Boolean aBoolean) {
				if (aBoolean) {
					FileInputStream inputStream = null;
					try {
						inputStream = new FileInputStream(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					startImg.setImageBitmap(bitmap);
				} else {
					Glide.with(GildeActivity.this)
						.load(R.mipmap.start_img)
						.diskCacheStrategy(DiskCacheStrategy.NONE)
						.centerCrop()
						.placeholder(R.mipmap.start_img)
						.error(R.mipmap.start_img)
						.crossFade()
						.into(startImg);
				}
				return null;
			}
		  }).map(new Func1<Void, Void>() {
			@Override public Void call(Void aVoid) {
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
				return null;
			}
		  }).delay(888, TimeUnit.MILLISECONDS)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Action1<Void>() {
			@Override public void call(Void aLong) {
				hText.animateText("DrizzleDaily");
			}
		});
	}
}
