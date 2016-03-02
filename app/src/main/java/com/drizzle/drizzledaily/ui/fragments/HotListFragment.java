package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.HotNews;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.ReadActivity;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.FabEvent;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;
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
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
		ApiBuilder.create(MyApi.class).hot().filter(new Func1<HotNews, Boolean>() {
			@Override public Boolean call(HotNews hotNews) {
				return NetUtils.isConnected(getActivity());
			}
		}).doOnSubscribe(new Action0() {
			@Override public void call() {
				swipeRefresh(true);
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).map(new Func1<HotNews, HotNews>() {
			@Override public HotNews call(HotNews hotNews) {
				for (HotNews.RecentEntity recent : hotNews.getRecent()) {
					BaseListItem baseListItem =
						new BaseListItem(recent.getNews_id(), recent.getTitle(), recent.getThumbnail(), false, "");
					hotListItems.add(baseListItem);
				}
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
			mListView.smoothScrollToPosition(0);
		}
	}
}
