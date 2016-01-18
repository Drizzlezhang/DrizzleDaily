package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.Sections;
import com.drizzle.drizzledaily.api.model.Themes;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.ThemeListActivity;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.FabEvent;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;

import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ThemeListActivity.class);
				intent.putExtra("themeid", themeItems.get(position).getId());
				startActivity(intent);
			}
		});
		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://静止状态
						EventBus.getDefault().post(new FabEvent(true));
						break;
					case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
						EventBus.getDefault().post(new FabEvent(false));
						break;
					case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
						EventBus.getDefault().post(new FabEvent(false));
						break;
					default:
						break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
		ApiBuilder.create(MyApi.class).themes()
			.filter(new Func1<Themes, Boolean>() {
				@Override public Boolean call(Themes themes) {
					return NetUtils.isConnected(getActivity());
				}
			})
			.doOnSubscribe(new Action0() {
				@Override public void call() {
					swipeRefresh(true);
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.map(new Func1<Themes, Themes>() {
				@Override public Themes call(Themes themes) {
					for (Themes.OthersEntity others :themes.getOthers()) {
						BaseListItem baseListItem =
							new BaseListItem(others.getId(), others.getName(), others.getThumbnail(), false, "",
								others.getDescription());
						themeItems.add(baseListItem);
					}
					return themes;
				}
			})
			.subscribeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<Themes>() {
				@Override public void onCompleted() {
					swipeRefresh(false);
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(getActivity(), "服务器出问题了");
					swipeRefresh(false);
				}

				@Override public void onNext(Themes themes) {
					adapter.notifyDataSetChanged();
				}
			});

	}

	public void onEvent(FabClickEvent fabClickEvent) {
		if (fabClickEvent.getFragmentId() == 3) {
			mGridView.smoothScrollToPosition(0);
		}
	}
}
