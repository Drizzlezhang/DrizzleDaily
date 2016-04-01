package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.GridRecyclerAdapter;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.Themes;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.ui.activities.ThemeListActivity;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
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

	@Bind(R.id.theme_grid) RecyclerView mRecyclerView;
	private List<BaseListItem> themeItems = new ArrayList<>();
	private GridRecyclerAdapter adapter;
	private static String THEMECACHE = "themelistcache";

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.theme_list_fragment, container, false);
		ButterKnife.bind(this, view);
		initViews();
		String themeCache = PerferUtils.getString(THEMECACHE);
		if (!themeCache.equals("")) {
			Gson gson = new Gson();
			List<BaseListItem> baseListItemList = gson.fromJson(themeCache, new TypeToken<List<BaseListItem>>() {
			}.getType());
			themeItems.addAll(baseListItemList);
			adapter.notifyDataSetChanged();
		}
		getLists();
		return view;
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
		adapter = new GridRecyclerAdapter(themeItems, getActivity());
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ThemeListActivity.class);
				intent.putExtra("themeid", themeItems.get(position).getId());
				ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
					new Pair<View, String>(view.findViewById(R.id.grid_item_img), getString(R.string.translation_img)));
				ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
			}
		});
		mRecyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
	}

	@Override public void onRefresh() {
		getLists();
	}

	/**
	 * 在页面切换时停止活动view
	 */
	@Override public void onHiddenChanged(boolean hidden) {
		if (hidden) {
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
		ApiBuilder.create(MyApi.class).themes().filter(new Func1<Themes, Boolean>() {
			@Override public Boolean call(Themes themes) {
				return NetUtils.isConnected(getActivity());
			}
		}).doOnSubscribe(new Action0() {
			@Override public void call() {
				swipeRefresh(true);
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).map(new Func1<Themes, Themes>() {
			@Override public Themes call(Themes themes) {
				themeItems.clear();
				for (Themes.OthersEntity others : themes.getOthers()) {
					BaseListItem baseListItem =
						new BaseListItem(others.getId(), others.getName(), others.getThumbnail(), false, "",
							others.getDescription());
					themeItems.add(baseListItem);
				}
				Gson gson = new Gson();
				PerferUtils.saveSth(THEMECACHE, gson.toJson(themeItems));
				return themes;
			}
		}).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Themes>() {
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
			mRecyclerView.smoothScrollToPosition(0);
		}
	}
}
