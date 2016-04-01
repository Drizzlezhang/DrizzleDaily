package com.drizzle.drizzledaily.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.SimpleRecyclerAdapter;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.ThemeList;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 主题日报列表activity
 */
public class ThemeListActivity extends BaseActivity {

	@Bind(R.id.theme_list) RecyclerView mRecyclerView;

	@Bind(R.id.theme_list_headimg) ImageView mImageView;

	@Bind(R.id.theme_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;

	@Bind(R.id.theme_list_progress) ProgressBar mProgressBar;

	private int themeId;
	private String imgUrl;
	private String title;
	private List<BaseListItem> themeList = new ArrayList<>();
	private SimpleRecyclerAdapter adapter;
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
		ApiBuilder.create(MyApi.class)
			.themelist(themeId)
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
					Glide.with(getApplicationContext())
						.load(imgUrl)
						.centerCrop()
						.error(R.mipmap.place_img)
						.crossFade()
						.into(mImageView);
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(ThemeListActivity.this, "服务器出问题了");
					mProgressBar.setVisibility(View.GONE);
				}

				@Override public void onNext(ThemeList list) {
					mProgressBar.setVisibility(View.GONE);
				}
			});
	}

	private void initViews() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.theme_list_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		toolbar.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mRecyclerView.smoothScrollToPosition(0);
			}
		});
		adapter = new SimpleRecyclerAdapter(this, themeList);
		mRecyclerView.setItemAnimator(new SlideInDownAnimator());
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.addItemDecoration(
			new HorizontalDividerItemDecoration.Builder(this).color(Color.GRAY).size(1).build());
		mRecyclerView.setAdapter(new SlideInBottomAnimationAdapter(adapter));
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ThemeListActivity.this, SectionReadActivity.class);
				intent.putExtra("readid", themeList.get(position).getId());
				startActivity(intent);
			}
		});
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(THEMEID, themeId);
		super.onSaveInstanceState(outState);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_single, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finishAfterTransition();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override public void onBackPressed() {
		super.onBackPressed();
		finishAfterTransition();
	}
}
