package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.HotNews;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.ReadActivity;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;

/**
 * 今日热门列表
 */
public class HotListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	@Bind(R.id.hot_list_refresh) SwipeRefreshLayout mRefreshLayout;

	@Bind(R.id.hot_list) ListView mListView;

	private List<BaseListItem> hotListItems = new ArrayList<>();
	private CommonAdapter<BaseListItem> adapter;
	private static final String HOTCACHENAME = "hotlistcache";

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hot_list_fragment, container, false);
		ButterKnife.bind(this, view);
		initViews();
		//String hotcachejson = PerferUtils.getString(HOTCACHENAME);
		//if (!hotcachejson.equals("")) {
		//	manageHotJson(hotcachejson);
		//}
		getLists();
		return view;
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ReadActivity.class);
				intent.putExtra(Config.READID, hotListItems.get(position).getId());
				startActivity(intent);
			}
		});
		adapter = new CommonAdapter<BaseListItem>(getActivity(), hotListItems, R.layout.base_list_item) {
			@Override public void convert(ViewHolder helper, BaseListItem item) {
				helper.setText(R.id.base_item_title, item.getTitle());
				helper.setImg(R.id.base_item_img, item.getImgUrl());
				helper.setText(R.id.base_item_date, "");
			}
		};
		mListView.setAdapter(adapter);
	}

	@Override public void onRefresh() {
		getLists();
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

	@Override public void onClickToolbar() {
		mListView.smoothScrollToPosition(0);
	}

	/**
	 * 请求数据并存入list
	 */
	private void getLists() {
		swipeRefresh(true);
		if (NetUtils.isConnected(getActivity())) {
			ApiBuilder.create(MyApi.class).hot().enqueue(new Callback<HotNews>() {
				@Override public void onResponse(Response<HotNews> response) {
					for (HotNews.RecentEntity recent : response.body().getRecent()) {
						BaseListItem baseListItem =
							new BaseListItem(recent.getNews_id(), recent.getTitle(), recent.getThumbnail(), false, "");
						hotListItems.add(baseListItem);
					}
					handler.sendEmptyMessage(0);
				}

				@Override public void onFailure(Throwable t) {
					TUtils.showShort(getActivity(), t.getMessage());
					mRefreshLayout.setRefreshing(false);
				}
			});
		} else {
			TUtils.showShort(getActivity(), "网络未连接");
			swipeRefresh(false);
		}
	}

	///**
	// * 处理请求到和或者缓存的最新数据
	// */
	//private void manageHotJson(String hotJson) {
	//	try {
	//		hotListItems.clear();
	//		JSONObject jsonObject = new JSONObject(hotJson);
	//		JSONArray recent = jsonObject.getJSONArray("recent");
	//		for (int i = 0; i < recent.length(); i++) {
	//			JSONObject story = recent.getJSONObject(i);
	//			int id = story.getInt("news_id");
	//			String title = story.getString("title");
	//			String imgUrl = story.getString("thumbnail");
	//			BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "");
	//			hotListItems.add(baseListItem);
	//		}
	//		handler.sendEmptyMessage(0);
	//	} catch (JSONException e) {
	//		e.printStackTrace();
	//		TUtils.showShort(getActivity(), "Json数据解析错误");
	//		mRefreshLayout.setRefreshing(false);
	//	}
	//}
}
