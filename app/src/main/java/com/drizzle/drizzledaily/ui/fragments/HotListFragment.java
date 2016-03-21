package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.ListRecyclerAdapter;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.HotNews;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.ReadActivity;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 今日热门列表
 */
public class HotListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	@Bind(R.id.hot_list_refresh) SwipeRefreshLayout mRefreshLayout;

	@Bind(R.id.hot_list) RecyclerView mRecyclerView;

	private List<BaseListItem> hotListItems = new ArrayList<>();
	private ListRecyclerAdapter adapter;
	private static final String HOTCACHENAME = "hotlistcache";

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hot_list_fragment, container, false);
		ButterKnife.bind(this, view);
		initViews();
		String hotcachejson = PerferUtils.getString(HOTCACHENAME);
		if (!hotcachejson.equals("")) {
			Gson gson = new Gson();
			List<BaseListItem> baseListItemList = gson.fromJson(hotcachejson, new TypeToken<List<BaseListItem>>() {
			}.getType());
			hotListItems.addAll(baseListItemList);
			adapter.notifyDataSetChanged();
		}
		getLists();
		return view;
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		adapter = new ListRecyclerAdapter(getActivity(), hotListItems);
		mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ReadActivity.class);
				intent.putExtra(Config.READID, hotListItems.get(position).getId());
				startActivity(intent);
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
		ApiBuilder.create(MyApi.class).hot().
			filter(new Func1<HotNews, Boolean>() {
				@Override public Boolean call(HotNews hotNews) {
					return NetUtils.isConnected(getActivity());
				}
			}).doOnSubscribe(new Action0() {
			@Override public void call() {
				swipeRefresh(true);
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).map(new Func1<HotNews, HotNews>() {
			@Override public HotNews call(HotNews hotNews) {
				hotListItems.clear();
				for (HotNews.RecentEntity recent : hotNews.getRecent()) {
					BaseListItem baseListItem =
						new BaseListItem(recent.getNews_id(), recent.getTitle(), recent.getThumbnail(), false, "");
					hotListItems.add(baseListItem);
				}
				Gson gson = new Gson();
				PerferUtils.saveSth(HOTCACHENAME, gson.toJson(hotListItems));
				return hotNews;
			}
		}).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<HotNews>() {
			@Override public void onCompleted() {
				swipeRefresh(false);
			}

			@Override public void onError(Throwable e) {
				TUtils.showShort(getActivity(), "服务器出问题了");
				swipeRefresh(false);
			}

			@Override public void onNext(HotNews hotNews) {
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void onEvent(FabClickEvent fabClickEvent) {
		if (fabClickEvent.getFragmentId() == 2) {
			mRecyclerView.smoothScrollToPosition(0);
		}
	}
}
