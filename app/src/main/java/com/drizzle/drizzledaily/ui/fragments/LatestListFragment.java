package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
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
import com.drizzle.drizzledaily.adapter.LatestRecyclerAdapter;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.BeforeNews;
import com.drizzle.drizzledaily.api.model.LatestNews;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.ReadActivity;
import com.drizzle.drizzledaily.utils.DateUtils;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.FabEvent;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 首页列表fragment，包括一个viewpager和一个listview
 */
public class LatestListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
	@Bind(R.id.latest_list_refresh) SwipeRefreshLayout mRefreshLayout;

	@Bind(R.id.latest_list) RecyclerView mRecyclerView;

	private Calendar mCalendar;
	private List<BaseListItem> baseListItems = new ArrayList<>();
	private List<BaseListItem> headpagerItems = new ArrayList<>();
	private LatestRecyclerAdapter latestAdapter;
	private static final String LATESTCACHENAME = "latestcache";
	private static final String HEADCACHENAME = "headcachename";

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.latest_list_fragment, container, false);
		ButterKnife.bind(this, view);
		mCalendar = Calendar.getInstance();
		initViews();
		String latestcache = PerferUtils.getString(LATESTCACHENAME);
		String headcache = PerferUtils.getString(HEADCACHENAME);
		if (!latestcache.equals("") && !headcache.equals("")) {
			Gson gson = new Gson();
			List<BaseListItem> baseListItemList = gson.fromJson(latestcache, new TypeToken<List<BaseListItem>>() {
			}.getType());
			baseListItems.addAll(baseListItemList);
			List<BaseListItem> headListItemList = gson.fromJson(headcache, new TypeToken<List<BaseListItem>>() {
			}.getType());
			headpagerItems.addAll(headListItemList);
			latestAdapter.notifyDataSetChanged();
		}
		swipeRefresh(true);
		getTodayNews();
		return view;
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		FragmentManager manager = getFragmentManager();
		latestAdapter = new LatestRecyclerAdapter(getActivity(), baseListItems, headpagerItems, manager);
		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(linearLayoutManager);
		mRecyclerView.setItemAnimator(new SlideInUpAnimator());
		latestAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ReadActivity.class);
				intent.putExtra(Config.READID, baseListItems.get(position - 1).getId());
				ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
					new Pair<View, String>(view.findViewById(R.id.base_item_img), getString(R.string.translation_img)));
				ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
			}
		});
		mRecyclerView.setAdapter(new SlideInRightAnimationAdapter(latestAdapter));
		mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					if (linearLayoutManager.findLastCompletelyVisibleItemPosition()
						== linearLayoutManager.getItemCount() - 1) {
						String time = DateUtils.printCalendar(mCalendar);
						getBeforeNews(time);
					}
					EventBus.getDefault().post(new FabEvent(true));
				} else {
					EventBus.getDefault().post(new FabEvent(false));
				}
			}
		});
	}

	/**
	 * 获取当天新闻
	 */
	private void getTodayNews() {
		ApiBuilder.create(MyApi.class)
			.latest()
			.filter(new Func1<LatestNews, Boolean>() {
				@Override public Boolean call(LatestNews latestNews) {
					return NetUtils.isConnected(getActivity());
				}
			})
			.doOnSubscribe(new Action0() {
				@Override public void call() {
					swipeRefresh(true);
				}
			})
			.
				observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe(new Observer<LatestNews>() {
				@Override public void onCompleted() {
					latestAdapter.notifyDataSetChanged();
					swipeRefresh(false);
					EventBus.getDefault().post(new FabEvent(true));
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(getActivity(), "服务器出问题了");
					swipeRefresh(false);
				}

				@Override public void onNext(LatestNews latestNews) {
					baseListItems.clear();
					headpagerItems.clear();
					BaseListItem todayNews = new BaseListItem();
					todayNews.setViewType(0);
					todayNews.setDate("今日热闻");
					baseListItems.add(todayNews);
					String date = DateUtils.printDate(mCalendar);
					for (LatestNews.TopStoriesEntity topStory : latestNews.getTop_stories()) {
						BaseListItem headbaseListItem =
							new BaseListItem(topStory.getId(), topStory.getTitle(), topStory.getImage(), false, "");
						headpagerItems.add(headbaseListItem);
					}
					Gson gson = new Gson();
					PerferUtils.saveSth(HEADCACHENAME, gson.toJson(headpagerItems));
					for (LatestNews.StoriesEntity story : latestNews.getStories()) {
						BaseListItem baseListItem =
							new BaseListItem(story.getId(), story.getTitle(), story.getImages().get(0), false, date);
						baseListItems.add(baseListItem);
					}
					PerferUtils.saveSth(LATESTCACHENAME, gson.toJson(baseListItems));
				}
			});
	}

	/**
	 * 获取往日新闻
	 */
	private void getBeforeNews(String time) {
		ApiBuilder.create(MyApi.class)
			.before(time)
			.filter(new Func1<BeforeNews, Boolean>() {
				@Override public Boolean call(BeforeNews beforeNews) {
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
			.map(new Func1<BeforeNews, BeforeNews>() {
				@Override public BeforeNews call(BeforeNews beforeNews) {
					String data = DateUtils.printDate(DateUtils.getBeforeDay(mCalendar));
					BaseListItem onedayNews = new BaseListItem();
					onedayNews.setViewType(0);
					onedayNews.setDate(data);
					baseListItems.add(onedayNews);
					mCalendar = DateUtils.getAfterDay(mCalendar);
					for (BeforeNews.StoriesEntity story : beforeNews.getStories()) {
						BaseListItem baseListItem =
							new BaseListItem(story.getId(), story.getTitle(), story.getImages().get(0), false, data);
						baseListItems.add(baseListItem);
					}
					mCalendar = DateUtils.getBeforeDay(mCalendar);
					return beforeNews;
				}
			})
			.subscribeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<BeforeNews>() {
				@Override public void onCompleted() {
					swipeRefresh(false);
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(getActivity(), "服务器出问题了");
					swipeRefresh(false);
				}

				@Override public void onNext(BeforeNews beforeNews) {
					latestAdapter.notifyDataSetChanged();
				}
			});
	}

	/**
	 * 刷新列表,重置当前时间
	 */
	@Override public void onRefresh() {
		getTodayNews();
		mCalendar = Calendar.getInstance();
	}

	public void onEvent(FabClickEvent fabClickEvent) {
		if (fabClickEvent.getFragmentId() == 1) {
			mRecyclerView.smoothScrollToPosition(0);
		}
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
}
