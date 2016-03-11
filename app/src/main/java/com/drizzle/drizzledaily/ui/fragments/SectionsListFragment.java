package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.drizzle.drizzledaily.api.model.Sections;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.ui.activities.SectionListActivity;
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
 * 专栏列表
 */
public class SectionsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
	@Bind(R.id.sections_grid_refresh) SwipeRefreshLayout mRefreshLayout;

	@Bind(R.id.sections_grid) RecyclerView mRecyclerView;

	private static String SECTIONCACHE = "sectionlistcache";

	private List<BaseListItem> sectionsItems = new ArrayList<>();
	private GridRecyclerAdapter adapter;

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sections_list_fragment, container, false);
		ButterKnife.bind(this, view);
		initViews();
		String sectionCache = PerferUtils.getString(SECTIONCACHE);
		if (!sectionCache.equals("")) {
			Gson gson = new Gson();
			List<BaseListItem> baseListItemList = gson.fromJson(sectionCache, new TypeToken<List<BaseListItem>>() {
			}.getType());
			sectionsItems.addAll(baseListItemList);
			adapter.notifyDataSetChanged();
		}
		getLists();
		return view;
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
		adapter = new GridRecyclerAdapter(sectionsItems, getActivity());
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), SectionListActivity.class);
				intent.putExtra("sectionid", sectionsItems.get(position).getId());
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
		ApiBuilder.create(MyApi.class).sections().filter(new Func1<Sections, Boolean>() {
			@Override public Boolean call(Sections sections) {
				return NetUtils.isConnected(getActivity());
			}
		}).doOnSubscribe(new Action0() {
			@Override public void call() {
				swipeRefresh(true);
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).map(new Func1<Sections, Sections>() {
			@Override public Sections call(Sections sections) {
				sectionsItems.clear();
				for (Sections.DataEntity data : sections.getData()) {
					BaseListItem baseListItem =
						new BaseListItem(data.getId(), data.getName(), data.getThumbnail(), false, "",
							data.getDescription());
					sectionsItems.add(baseListItem);
				}
				Gson gson = new Gson();
				PerferUtils.saveSth(SECTIONCACHE, gson.toJson(sectionsItems));
				return sections;
			}
		}).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Sections>() {
			@Override public void onCompleted() {
				swipeRefresh(false);
			}

			@Override public void onError(Throwable e) {
				TUtils.showShort(getActivity(), "服务器出问题了");
				swipeRefresh(false);
			}

			@Override public void onNext(Sections sections) {
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void onEvent(FabClickEvent fabClickEvent) {
		if (fabClickEvent.getFragmentId() == 4) {
			mRecyclerView.smoothScrollToPosition(0);
		}
	}
}