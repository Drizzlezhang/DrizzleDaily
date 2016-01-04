package com.drizzle.drizzledaily.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.BeforeNews;
import com.drizzle.drizzledaily.api.model.ThemeList;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.bean.ShareBean;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 主题日报列表activity
 */
public class ThemeListActivity extends BaseActivity {

	private Toolbar mToolbar;

	@Bind(R.id.theme_list) ListView mListView;

	@Bind(R.id.theme_list_headimg) ImageView mImageView;

	@Bind(R.id.theme_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;

	@Bind(R.id.theme_list_progress) AVLoadingIndicatorView loadingIndicatorView;

	@Bind(R.id.theme_list_scroll) NestedScrollView mNestedScrollView;
	private int themeId;
	private String imgUrl;
	private String title;
	private List<BaseListItem> themeList = new ArrayList<>();
	private CommonAdapter<BaseListItem> adapter;
	private static final String THEMEID = "themeid";

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_theme);
		ButterKnife.bind(this);
		if (savedInstanceState != null) {
			themeId = savedInstanceState.getInt(THEMEID);
		} else {
			themeId = getIntent().getIntExtra(THEMEID, -1);
		}
		initViews();
		getLists();
	}

	private void getLists() {
		ApiBuilder.create(MyApi.class).themelist(themeId)
			.subscribeOn(Schedulers.io())
			.filter(new Func1<ThemeList, Boolean>() {
				@Override public Boolean call(ThemeList themeList) {
					return NetUtils.isConnected(ThemeListActivity.this);
				}
			})
			.observeOn(Schedulers.io())
			.map(new Func1<ThemeList, ThemeList>() {
				@Override public ThemeList call(ThemeList list) {
					for (ThemeList.StoriesEntity stories : list.getStories()) {
						BaseListItem baseListItem =
							new BaseListItem(stories.getId(), stories.getTitle(), "", false, "");
						themeList.add(baseListItem);
					}
					imgUrl = list.getImage();
					title = list.getName();
					return list;
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<ThemeList>() {
				@Override public void onCompleted() {
					collapsingToolbarLayout.setTitle(title);
					adapter.notifyDataSetChanged();
					setListViewHeightBasedOnChildren(mListView);
					Glide.with(getApplicationContext())
						.load(imgUrl)
						.centerCrop()
						.error(R.mipmap.place_img)
						.crossFade()
						.into(mImageView);

				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(ThemeListActivity.this, "服务器出问题了");
					loadingIndicatorView.setVisibility(View.GONE);
				}

				@Override public void onNext(ThemeList list) {
					loadingIndicatorView.setVisibility(View.GONE);
				}
			});
	}

	private void initViews() {
		mToolbar = (Toolbar) findViewById(R.id.theme_list_toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mNestedScrollView.smoothScrollTo(0, 0);
			}
		});
		mListView.setDivider(null);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ThemeListActivity.this, SectionReadActivity.class);
				intent.putExtra("readid", themeList.get(position).getId());
				startActivity(intent);
			}
		});
		adapter = new CommonAdapter<BaseListItem>(ThemeListActivity.this, themeList, R.layout.simple_list_item) {
			@Override public void convert(ViewHolder helper, BaseListItem item) {
				helper.setText(R.id.simple_item_title, item.getTitle());
			}
		};
		mListView.setAdapter(adapter);
	}


	@Override protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(THEMEID, themeId);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 测量listview高度，解决和scrollview冲突问题
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_single, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
