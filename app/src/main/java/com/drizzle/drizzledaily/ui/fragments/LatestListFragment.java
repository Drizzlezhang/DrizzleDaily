package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.LatestAdapter;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.BeforeNews;
import com.drizzle.drizzledaily.api.model.LatestNews;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.MainActivity;
import com.drizzle.drizzledaily.ui.activities.ReadActivity;
import com.drizzle.drizzledaily.utils.DateUtils;
import com.drizzle.drizzledaily.utils.FabClickEvent;
import com.drizzle.drizzledaily.utils.FabEvent;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;

import com.drizzle.drizzledaily.view.PageScrollingIndicator;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Observable;
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

	//自动轮播viewpager
	@Bind(R.id.latest_list) ListView mListView;
	private AutoScrollViewPager mViewPager;

	private Calendar mCalendar;
	private List<BaseListItem> baseListItems = new ArrayList<>();
	private List<BaseListItem> headpagerItems = new ArrayList<>();
	private LatestAdapter latestAdapter;
	private FragmentStatePagerAdapter fragmentStatePagerAdapter;
	private static final String LATESTCACHENAME = "latestcache";

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.latest_list_fragment, container, false);
		ButterKnife.bind(this, view);
		View headview = inflater.inflate(R.layout.head_viewpager, null, true);
		mViewPager = (AutoScrollViewPager) headview.findViewById(R.id.head_viewpager);
		mListView.addHeaderView(headview);
		mCalendar = Calendar.getInstance();
		initViews();
		//String latestcache = PerferUtils.getString(LATESTCACHENAME);
		//if (latestcache.equals("")) {
		//    //TODO
		//} else {
		//    manageLatestJson(latestcache);
		//}
		swipeRefresh(true);
		getTodayNews();
		return view;
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://静止状态
						if (view.getLastVisiblePosition() == view.getCount() - 1) {
							String time = DateUtils.printCalendar(mCalendar);
							getBeforeNews(time);
						}
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
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (baseListItems.get(position - 1).getViewType() == 0) {
					//TODO
				} else {
					Intent intent = new Intent(getActivity(), ReadActivity.class);
					intent.putExtra(Config.READID, baseListItems.get(position - 1).getId());
					startActivity(intent);
				}
			}
		});
		latestAdapter = new LatestAdapter(getActivity(), baseListItems);
		mListView.setAdapter(latestAdapter);
	}

	private Handler handler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					latestAdapter.notifyDataSetChanged();
					mRefreshLayout.setRefreshing(false);
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 获取当天新闻
	 */
	private void getTodayNews() {
		ApiBuilder.create(MyApi.class).latest().filter(new Func1<LatestNews, Boolean>() {
			@Override public Boolean call(LatestNews latestNews) {
				return NetUtils.isConnected(getActivity());
			}
		}).doOnSubscribe(new Action0() {
			@Override public void call() {
				swipeRefresh(true);
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<LatestNews>() {
			@Override public void onCompleted() {
				final FragmentManager manager = getChildFragmentManager();
				fragmentStatePagerAdapter = new FragmentStatePagerAdapter(manager) {
					@Override public Fragment getItem(int position) {
						BaseListItem baseListItem = headpagerItems.get(position);
						HeadPagerFragment pagerFragment =
							HeadPagerFragment.newInstance(baseListItem.getImgUrl(), baseListItem.getTitle(),
								baseListItem.getId());
						return pagerFragment;
					}

					@Override public int getCount() {
						return headpagerItems.size();
					}
				};
				mViewPager.setInterval(4000);
				mViewPager.setStopScrollWhenTouch(true);
				mViewPager.setAdapter(fragmentStatePagerAdapter);
				mViewPager.startAutoScroll(5000);
				handler.sendEmptyMessageDelayed(1, 100);
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
				for (LatestNews.StoriesEntity story : latestNews.getStories()) {
					BaseListItem baseListItem =
						new BaseListItem(story.getId(), story.getTitle(), story.getImages().get(0), false, date);
					baseListItems.add(baseListItem);
				}
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
			mListView.smoothScrollToPosition(0);
		}
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
					Log.d("refresh", "true");
					mRefreshLayout.setRefreshing(true);
				} else {
					mRefreshLayout.setRefreshing(false);
				}
			}
		});
	}
}
