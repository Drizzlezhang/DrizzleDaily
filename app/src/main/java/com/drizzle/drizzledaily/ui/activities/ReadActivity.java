package com.drizzle.drizzledaily.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.BeforeNews;
import com.drizzle.drizzledaily.api.model.Story;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.bean.ShareBean;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.DateUtils;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Body;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 阅读文章主界面
 */
public class ReadActivity extends BaseActivity {

	private int readid;
	private String ImgUrl;
	private String body;
	private String pagetltle;
	private String cssadd;
	private String pageUrl;

	@Bind(R.id.read_toolbar) Toolbar mToolbar;

	@Bind(R.id.read_headimg) ImageView headImg;

	@Bind(R.id.read_imgres) TextView readImgres;

	@Bind(R.id.read_webview) WebView readWeb;

	@Bind(R.id.read_collapsing) CollapsingToolbarLayout collapsingToolbarLayout;

	@Bind(R.id.read_progress) AVLoadingIndicatorView loadingIndicatorView;

	@Bind(R.id.read_scroll) NestedScrollView mNestedScrollView;
	private DialogPlus dialogPlus;
	private CommonAdapter<ShareBean> adapter;
	private Set<CollectBean> collectBeanSet;
	private List<ShareBean> shareBeanList = new ArrayList<>();
	private Bitmap shareBitmap;

	private static final String APP_CACHE_DIRNAME = "/webcache"; // web缓存目录

	private Handler handler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:

					break;
				default:
					break;
			}
		}
	};

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		setContentView(R.layout.activity_read);
		ButterKnife.bind(this);
		initData();
		if (savedInstanceState != null) {
			readid = savedInstanceState.getInt(Config.READID);
		} else {
			Intent intent = getIntent();
			readid = intent.getIntExtra(Config.READID, -1);
		}
		initViews();
	}

	private void initData() {
		SharedPreferences sharedPreferences = getSharedPreferences(Config.CACHE_DATA, MODE_PRIVATE);
		String collectcache = sharedPreferences.getString(Config.COLLECTCACHE, "[]");
		Gson gson = new Gson();
		collectBeanSet = gson.fromJson(collectcache, new TypeToken<Set<CollectBean>>() {
		}.getType());
		ShareBean bean1 = new ShareBean(R.mipmap.frends, "朋友圈");
		ShareBean bean2 = new ShareBean(R.mipmap.weixin, "微信好友");
		shareBeanList.add(bean1);
		shareBeanList.add(bean2);
		adapter = new CommonAdapter<ShareBean>(this, shareBeanList, R.layout.share_list_item) {
			@Override public void convert(ViewHolder helper, ShareBean item) {
				helper.setText(R.id.share_item_text, item.getText());
				helper.setImgByid(R.id.share_item_img, item.getImgId());
			}
		};
		shareBitmap = null;
	}

	private void initViews() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mNestedScrollView.smoothScrollTo(0, 0);
			}
		});
		dialogPlus = DialogPlus.newDialog(ReadActivity.this)
			.setAdapter(adapter)
			.setHeader(R.layout.share_head)
			.setOnBackPressListener(new OnBackPressListener() {
				@Override public void onBackPressed(DialogPlus dialogPlus) {
					dialogPlus.dismiss();
				}
			})
			.setOnItemClickListener(new OnItemClickListener() {
				@Override public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
					if (shareBitmap == null) {
						shareBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.labal_icon);
					}
					if (position == 1) {
						wechatShare(1, pagetltle, pageUrl, shareBitmap); //分享到朋友圈
					} else if (position == 2) {
						wechatShare(0, pagetltle, pageUrl, shareBitmap); //分享给微信好友
					}
					dialogPlus.dismiss();
				}
			})
			.setCancelable(true)
			.setPadding(20, 30, 20, 20)
			.create();
		if (NetUtils.isConnected(ReadActivity.this)) {
			initWebView(true);
			getAtrical(readid);
		} else {
			initWebView(false);
			TUtils.showShort(ReadActivity.this, "网络未连接");
			loadingIndicatorView.setVisibility(View.GONE);
		}
	}

	public void initWebView(boolean isnet) {
		readWeb.getSettings().setJavaScriptEnabled(true);
	}

	/**
	 * 处理readjson数据
	 */
	private void getAtrical(int managerReadId) {
		ApiBuilder.create(MyApi.class).story(managerReadId)
			.filter(new Func1<Story, Boolean>() {
				@Override public Boolean call(Story story) {
					return NetUtils.isConnected(ReadActivity.this);
				}
			})
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.newThread())
			.map(new Func1<Story, Story>() {
				@Override public Story call(Story story) {
					ImgUrl = story.getImage();
					try {
						shareBitmap = Glide.with(getApplicationContext())
							.load(ImgUrl)
							.asBitmap()
							.centerCrop()
							.into(100, 100)
							.get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					return story;
				}
			})
			.observeOn(Schedulers.io())
			.map(new Func1<Story, Story>() {
				@Override public Story call(Story story) {
					String name = story.getTitle();
					pagetltle = name;
					body = story.getBody();
					pageUrl = story.getShare_url();
					cssadd = story.getCss().get(0);
					return story;
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<Story>() {
				@Override public void onCompleted() {
					loadingIndicatorView.setVisibility(View.GONE);
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(ReadActivity.this, e.toString());
					loadingIndicatorView.setVisibility(View.GONE);
				}

				@Override public void onNext(Story story) {
					Glide.with(getApplicationContext())
						.load(ImgUrl)
						.centerCrop()
						.error(R.mipmap.place_img)
						.crossFade()
						.into(headImg);
					collapsingToolbarLayout.setTitle(pagetltle);
					readImgres.setText(story.getImage_source());
					String css = "<link rel=\"stylesheet\" href=\"" + cssadd + "type=\"text/css\">";
					String html = "<html><head>" + css + "</head><body>" + body + "</body></html>";
					html = html.replace("<div class=\"img-place-holder\">", "");
					readWeb.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
				}
			});
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(Config.READID, readid);
		super.onSaveInstanceState(outState);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_read, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_collect:
				int savetime = (int) System.currentTimeMillis();
				final CollectBean bean = new CollectBean(readid, pagetltle, 1, savetime);
				collectBeanSet.add(bean);
				final Gson gson = new Gson();
				PerferUtils.saveSth(Config.COLLECTCACHE, gson.toJson(collectBeanSet));
				new SnackBar.Builder(this).withOnClickListener(new SnackBar.OnMessageClickListener() {
					@Override public void onMessageClick(Parcelable token) {
						collectBeanSet.remove(bean);
						PerferUtils.saveSth(Config.COLLECTCACHE, gson.toJson(collectBeanSet));
						TUtils.showShort(ReadActivity.this, "已取消收藏");
					}
				})
					.withMessage("已收藏到本地文件夹。")
					.withActionMessage("取消")
					.withTextColorId(R.color.colorAccent)
					.withDuration(SnackBar.LONG_SNACK)
					.show();
				break;
			case R.id.action_share:
				dialogPlus.show();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
