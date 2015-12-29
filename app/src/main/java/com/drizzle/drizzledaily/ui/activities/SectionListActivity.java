package com.drizzle.drizzledaily.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import retrofit.Callback;
import retrofit.Response;

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
		if (NetUtils.isConnected(SectionListActivity.this)) {
			ApiBuilder.create(MyApi.class).sectionlist(sectionid).enqueue(new Callback<SectionList>() {
				@Override public void onResponse(Response<SectionList> response) {
					for (SectionList.StoriesEntity stories : response.body().getStories()) {
						BaseListItem baseListItem =
							new BaseListItem(stories.getId(), stories.getTitle(), stories.getImages().get(0), false,
								stories.getDate());
						sectionList.add(baseListItem);
					}
					title = response.body().getName();
					handler.sendEmptyMessage(0);
				}

				@Override public void onFailure(Throwable t) {
					TUtils.showShort(SectionListActivity.this, "服务器出问题了");
					loadingIndicatorView.setVisibility(View.GONE);
				}
			});
		} else {
			TUtils.showShort(SectionListActivity.this, "网络未连接");
			loadingIndicatorView.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					mToolbar.setTitle(title);
					adapter.notifyDataSetChanged();
					loadingIndicatorView.setVisibility(View.GONE);
					break;
				default:
					break;
			}
		}
	};

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
