package com.drizzle.drizzledaily.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.SectionList;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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
	private CommonAdapter<BaseListItem> adapter;

	@Bind(R.id.section_list_toolbar) Toolbar mToolbar;

	@Bind(R.id.section_list_listview) ListView mListView;

	@Bind(R.id.section_list_progress) AVLoadingIndicatorView loadingIndicatorView;

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
				mListView.smoothScrollToPosition(0);
			}
		});
		mListView.setDivider(null);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SectionListActivity.this, ReadActivity.class);
				intent.putExtra(Config.READID, sectionList.get(position).getId());
				startActivity(intent);
			}
		});
		adapter = new CommonAdapter<BaseListItem>(getApplicationContext(), sectionList, R.layout.base_list_item) {
			@Override public void convert(ViewHolder helper, BaseListItem item) {
				helper.setText(R.id.base_item_title, item.getTitle());
				helper.setImg(R.id.base_item_img, item.getImgUrl());
				helper.setText(R.id.base_item_date, item.getDate());
			}
		};
		mListView.setAdapter(adapter);
	}

	private void getList() {
		ApiBuilder.create(MyApi.class).sectionlist(sectionid)
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
					loadingIndicatorView.setVisibility(View.GONE);
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(SectionListActivity.this, "服务器出问题了");
					loadingIndicatorView.setVisibility(View.GONE);
				}

				@Override public void onNext(SectionList list) {
					mToolbar.setTitle(title);
					adapter.notifyDataSetChanged();
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
