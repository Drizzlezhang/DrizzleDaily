package com.drizzle.drizzledaily.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.StartImg;
import com.drizzle.drizzledaily.model.MyApplication;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.hanks.htextview.HTextView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
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
		ApiBuilder.create(MyApi.class).startImage("1080*1776").enqueue(new Callback<StartImg>() {
			@Override public void onResponse(Response<StartImg> response, Retrofit retrofit) {
				final String imgurl = response.body().getImg();
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
						downFile(s);
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
			}

			@Override public void onFailure(Throwable t) {

			}
		});
	}

	/**
	 * 引导页动画效果
	 */
	private void playAnim() {
		File filefolder = MyApplication.getContext().getExternalFilesDir("image");
		if (!filefolder.exists()) {
			filefolder.mkdirs();
		}
		final File file = new File(filefolder, "startimg.jpg");
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
						overridePendingTransition(0, android.R.anim.fade_in);
						finish();
					}
				});
				return null;
			}
		}).delay(888, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Void>() {
			@Override public void call(Void aLong) {
				hText.animateText("知乎日报");
			}
		});
	}

	/**
	 * 下载文件方法
	 */
	private void downFile(String url) {
		OkHttpClient mOkHttpClient = new OkHttpClient();
		final Request request = new Request.Builder().url(url).build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new com.squareup.okhttp.Callback() {
			@Override public void onFailure(Request request, IOException e) {

			}

			@Override public void onResponse(com.squareup.okhttp.Response response) throws IOException {
				Log.d("path",MyApplication.getContext().getExternalFilesDir("image").getPath());
				saveFile(response, MyApplication.getContext().getExternalFilesDir("image").getPath(), "startimg.jpg");
			}
		});
	}

	private File saveFile(com.squareup.okhttp.Response response, String destFileDir, String destFileName)
		throws IOException {
		InputStream is = null;
		byte[] buf = new byte[2048];
		int len = 0;
		FileOutputStream fos = null;
		try {
			is = response.body().byteStream();

			File dir = new File(destFileDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, destFileName);
			fos = new FileOutputStream(file);
			while ((len = is.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			fos.flush();

			return file;
		} finally {
			try {
				if (is != null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
