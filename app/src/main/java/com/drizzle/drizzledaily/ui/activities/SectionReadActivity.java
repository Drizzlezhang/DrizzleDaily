package com.drizzle.drizzledaily.ui.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.bean.ShareBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.OkHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专栏日报阅读界面（没有大图提供,单开一个页面）
 */

public class SectionReadActivity extends BaseActivity {

	@Bind(R.id.section_read_toolbar) Toolbar mToolbar;

	@Bind(R.id.section_read_webview) WebView sectionWeb;

	@Bind(R.id.section_read_progress) AVLoadingIndicatorView loadingIndicatorView;

	private String body;
	private int readid;
	private int themeid;
	private String themename;
	private String pagetitle;
	private CollectDB collectDB;
	private String cssadd;
	private String pageUrl;
	private Set<CollectBean> collectBeanSet;

	private DialogPlus dialogPlus;
	private CommonAdapter<ShareBean> adapter;
	private List<ShareBean> shareBeanList = new ArrayList<>();

	private Handler handler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					//webview加载css和html
					String css = "<link rel=\"stylesheet\" href=\"" + cssadd + "type=\"text/css\">";
					String html = "<html><head>" + css + "</head><body>" + body + "</body></html>";
					html = html.replace("<div class=\"img-place-holder\">", "");
					sectionWeb.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
					break;
				default:
					break;
			}
		}
	};

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section_read);
		ButterKnife.bind(this);
		initData();
		collectDB = CollectDB.getInstance(this);
		if (savedInstanceState != null) {
			readid = savedInstanceState.getInt(Config.READID);
		} else {
			readid = getIntent().getIntExtra(Config.READID, -1);
		}
		initViews();
		if (NetUtils.isConnected(SectionReadActivity.this)) {
			OkHttpUtils.get().url(Config.NEWS_BODY + readid).build().execute(new StringCallback() {
				@Override public void onError(Request request, Exception e) {
					TUtils.showShort(SectionReadActivity.this, "服务器出问题了");
					loadingIndicatorView.setVisibility(View.GONE);
				}

				@Override public void onResponse(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						String name = jsonObject.getString("title");
						mToolbar.setTitle(name);
						pagetitle = name;
						body = jsonObject.getString("body");
						JSONObject theme = jsonObject.getJSONObject("theme");
						themeid = theme.getInt("id");
						themename = theme.getString("name");
						cssadd = jsonObject.getJSONArray("css").getString(0);
						pageUrl = jsonObject.getString("share_url");
						handler.sendEmptyMessage(0);
						loadingIndicatorView.setVisibility(View.GONE);
					} catch (JSONException e) {
						e.printStackTrace();
						TUtils.showShort(SectionReadActivity.this, "json error");
						loadingIndicatorView.setVisibility(View.GONE);
					}
				}
			});
		} else {
			TUtils.showShort(SectionReadActivity.this, "网络未连接");
			loadingIndicatorView.setVisibility(View.GONE);
		}
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
	}

	private void initViews() {
		mToolbar.setTitle("载入中");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		sectionWeb.getSettings().setJavaScriptEnabled(true);
		dialogPlus = DialogPlus.newDialog(SectionReadActivity.this)
			.setAdapter(adapter)
			.setHeader(R.layout.share_head)
			.setOnBackPressListener(new OnBackPressListener() {
				@Override public void onBackPressed(DialogPlus dialogPlus) {
					dialogPlus.dismiss();
				}
			})
			.setOnItemClickListener(new OnItemClickListener() {
				@Override public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
					if (position == 1) {
						wechatShare(1, pagetitle, pageUrl,null); //分享到朋友圈
					} else if (position == 2) {
						wechatShare(0, pagetitle, pageUrl,null);//分享给微信好友
					}
					dialogPlus.dismiss();
				}
			})
			.setCancelable(true)
			.setPadding(20, 30, 20, 20)
			.create();
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
				final CollectBean bean = new CollectBean(readid, pagetitle, 2, savetime);
				collectBeanSet.add(bean);
				final Gson gson = new Gson();
				PerferUtils.saveSth(Config.COLLECTCACHE, gson.toJson(collectBeanSet));
				new SnackBar.Builder(this).withOnClickListener(new SnackBar.OnMessageClickListener() {
					@Override public void onMessageClick(Parcelable token) {
						collectBeanSet.remove(bean);
						PerferUtils.saveSth(Config.COLLECTCACHE, gson.toJson(collectBeanSet));
						TUtils.showShort(SectionReadActivity.this, "已取消收藏");
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