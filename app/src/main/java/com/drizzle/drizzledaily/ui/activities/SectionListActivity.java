package com.drizzle.drizzledaily.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.SimpleRecyclerAdapter;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.SectionList;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
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
 * 专栏列表activity
 */
public class SectionListActivity extends BaseActivity {

	private int sectionid;
	private static final String SECTIONID = "sectionid";
	private List<BaseListItem> sectionList = new ArrayList<>();
	private SimpleRecyclerAdapter adapter;

	@Bind(R.id.section_list_toolbar) Toolbar mToolbar;

	@Bind(R.id.section_list_listview) RecyclerView mRecyclerView;

	@Bind(R.id.section_list_progress) ProgressBar mProgressBar;

	private String title;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section_list);
		ButterKnife.bind(this);
		if (savedInstanceState != null) {
			sectionid = savedInstanceState.getInt(SECTIONID);
		} else {
			sectionid = getIntent().getIntExtra(SECTIONID, -1);
		}
		initViews();
		getList();
	}

	private void initViews() {
		mToolbar.setTitle("");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mRecyclerView.smoothScrollToPosition(0);
			}
		});
		adapter = new SimpleRecyclerAdapter(this,sectionList);
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SectionListActivity.this, ReadActivity.class);
				intent.putExtra(Config.READID, sectionList.get(position).getId());
				startActivity(intent);
			}
		});
		mRecyclerView.setItemAnimator(new SlideInDownAnimator());
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.addItemDecoration(
			new HorizontalDividerItemDecoration.Builder(this).color(Color.GRAY).size(1).build());
		mRecyclerView.setAdapter(new SlideInBottomAnimationAdapter(adapter));
	}

	private void getList() {
		ApiBuilder.create(MyApi.class)
			.sectionlist(sectionid)
			.filter(new Func1<SectionList, Boolean>() {
				@Override public Boolean call(SectionList list) {
					return NetUtils.isConnected(SectionListActivity.this);
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.map(new Func1<SectionList, SectionList>() {
				@Override public SectionList call(SectionList list) {
					for (SectionList.StoriesEntity stories : list.getStories()) {
						BaseListItem baseListItem =
							new BaseListItem(stories.getId(), stories.getTitle(), stories.getImages().get(0), false,
								stories.getDate());
						sectionList.add(baseListItem);
					}
					title = list.getName();
					return list;
				}
			})
			.subscribeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<SectionList>() {
				@Override public void onCompleted() {
					adapter.notifyDataSetChanged();
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(SectionListActivity.this, "服务器出问题了");
					mProgressBar.setVisibility(View.GONE);
				}

				@Override public void onNext(SectionList list) {
					mToolbar.setTitle(title);
					mProgressBar.setVisibility(View.GONE);
				}
			});
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

	@Override protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(SECTIONID, sectionid);
		super.onSaveInstanceState(outState);
	}
}
