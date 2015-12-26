package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.ThemeListActivity;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主题日报列表
 */
public class ThemeListFragment extends BaseFragment
	implements SwipeRefreshLayout.OnRefreshListener{
	@Bind(R.id.theme_grid_refresh) SwipeRefreshLayout mRefreshLayout;

	@Bind(R.id.theme_grid) GridView mGridView;
	private List<BaseListItem> themeItems = new ArrayList<>();
	private CommonAdapter<BaseListItem> adapter;
	private static String THEMECACHE = "themelistcache";

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.theme_list_fragment, container, false);
		ButterKnife.bind(this, view);
		initViews();
		String themecachejson = PerferUtils.getString(THEMECACHE);
		if (themecachejson.equals("")) {
			//TODO
		} else {
			manageThemeJson(themecachejson);
		}
		getLists(Config.THEME_LIST);
		return view;
	}

	@Override public void onClickToolbar() {
		mGridView.smoothScrollToPosition(0);
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ThemeListActivity.class);
				intent.putExtra("themeid", themeItems.get(position).getId());
				startActivity(intent);
			}
		});
		adapter = new CommonAdapter<BaseListItem>(getActivity(), themeItems, R.layout.base_grid_item) {
			@Override public void convert(ViewHolder helper, BaseListItem item) {
				helper.setText(R.id.grid_item_title, item.getTitle());
				helper.setImg(R.id.grid_item_img, item.getImgUrl());
				helper.setText(R.id.grid_item_describe, item.getDescribe());
			}
		};
		mGridView.setAdapter(adapter);
	}

	private Handler handler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					adapter.notifyDataSetChanged();
					mRefreshLayout.setRefreshing(false);
					break;
				default:
					break;
			}
		}
	};

	@Override public void onRefresh() {
		getLists(Config.THEME_LIST);
	}

	/**
	 * 在页面切换时停止活动view
	 */
	@Override public void onHiddenChanged(boolean hidden) {
		if (hidden == true) {
			if (mRefreshLayout.isRefreshing()) {
				mRefreshLayout.setRefreshing(false);
			}
		}
		super.onHiddenChanged(hidden);
	}

	/**
	 * swiperefresh在主线程中无法消失，需要新开线程
	 */
	private void swipeRefresh(final boolean refresh) {
		mRefreshLayout.post(new Runnable() {
			@Override public void run() {
				if (refresh) {
					mRefreshLayout.setRefreshing(true);
				} else {
					mRefreshLayout.setRefreshing(false);
				}
			}
		});
	}

	/**
	 * 请求数据并存入list
	 */
	private void getLists(final String listUrl) {
		swipeRefresh(true);
		if (NetUtils.isConnected(getActivity())) {
			OkHttpUtils.get().url(listUrl).build().execute(new StringCallback() {
				@Override public void onError(Request request, Exception e) {
					TUtils.showShort(getActivity(), "服务器出问题了");
					mRefreshLayout.setRefreshing(false);
				}

				@Override public void onResponse(String response) {
					PerferUtils.saveSth(THEMECACHE,response);
					manageThemeJson(response);
				}
			});
		} else {
			TUtils.showShort(getActivity(), "网络未连接");
			swipeRefresh(false);
		}
	}

	/**
	 * 处理json数据
	 */
	private void manageThemeJson(String themeJson) {
		try {
			themeItems.clear();
			JSONObject jsonObject = new JSONObject(themeJson);
			JSONArray others = jsonObject.getJSONArray("others");
			for (int i = 0; i < others.length(); i++) {
				JSONObject story = others.getJSONObject(i);
				int id = story.getInt("id");
				String title = story.getString("name");
				String imgUrl = story.getString("thumbnail");
				String describe = story.getString("description");
				BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "", describe);
				themeItems.add(baseListItem);
			}
			handler.sendEmptyMessage(0);
		} catch (JSONException e) {
			e.printStackTrace();
			TUtils.showShort(getActivity(), "Json数据解析错误");
			mRefreshLayout.setRefreshing(false);
		}
	}
}
