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
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.Themes;
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
import retrofit.Callback;
import retrofit.Response;

/**
 * 主题日报列表
 */
public class ThemeListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

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
		//String themecachejson = PerferUtils.getString(THEMECACHE);
		//if (themecachejson.equals("")) {
		//	//TODO
		//} else {
		//	manageThemeJson(themecachejson);
		//}
		getLists();
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
	private void getLists() {
		swipeRefresh(true);
		if (NetUtils.isConnected(getActivity())) {
			ApiBuilder.create(MyApi.class).themes().enqueue(new Callback<Themes>() {
				@Override public void onResponse(Response<Themes> response) {
					for (Themes.OthersEntity others:response.body().getOthers()){
						BaseListItem baseListItem =
							new BaseListItem(others.getId(), others.getName(), others.getThumbnail(), false, "", others.getDescription());
						themeItems.add(baseListItem);
					}
					handler.sendEmptyMessage(0);
				}

				@Override public void onFailure(Throwable t) {
					TUtils.showShort(getActivity(), "服务器出问题了");
					mRefreshLayout.setRefreshing(false);
				}
			});
		} else {
			TUtils.showShort(getActivity(), "网络未连接");
			swipeRefresh(false);
		}
	}

}
