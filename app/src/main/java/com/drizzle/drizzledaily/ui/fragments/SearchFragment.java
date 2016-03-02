package com.drizzle.drizzledaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.BeforeNews;
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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 用于显示根据日期查找到的日报数据
 */
public class SearchFragment extends BaseFragment {

	@Bind(R.id.search_list) ListView mListView;

	@Bind(R.id.search_progress) ProgressBar mProgressBar;

	private CommonAdapter<BaseListItem> adapter;
	private String id;
	private List<BaseListItem> baseListItems = new ArrayList<>();
	private static final String TIMEID = "timeid";

	public static SearchFragment newInstance(String timeid) {
		Bundle args = new Bundle();
		args.putString(TIMEID, timeid);
		SearchFragment searchFragment = new SearchFragment();
		searchFragment.setArguments(args);
		return searchFragment;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			id = savedInstanceState.getString(TIMEID);
		} else {
			id = getArguments().getString(TIMEID);
		}
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search, container, false);
		ButterKnife.bind(this, view);
		initViews();
		getLists();
		return view;
	}

	private void initViews() {
		adapter = new CommonAdapter<BaseListItem>(getActivity(), baseListItems, R.layout.base_list_item) {
			@Override public void convert(ViewHolder helper, BaseListItem item) {
				helper.setText(R.id.base_item_title, item.getTitle());
				helper.setImg(R.id.base_item_img, item.getImgUrl());
			}
		};
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ReadActivity.class);
				intent.putExtra(Config.READID, baseListItems.get(position).getId());
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
	}

	private void getLists() {
		ApiBuilder.create(MyApi.class)
			.before(id)
			.filter(new Func1<BeforeNews, Boolean>() {
				@Override public Boolean call(BeforeNews beforeNews) {
					return NetUtils.isConnected(getActivity());
				}
			})
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.map(new Func1<BeforeNews, BeforeNews>() {
				@Override public BeforeNews call(BeforeNews beforeNews) {
					for (BeforeNews.StoriesEntity stories : beforeNews.getStories()) {
						BaseListItem baseListItem =
							new BaseListItem(stories.getId(), stories.getTitle(), stories.getImages().get(0), false,
								"");
						baseListItems.add(baseListItem);
					}
					return beforeNews;
				}
			})
			.subscribeOn(AndroidSchedulers.mainThread())
			.subscribe(new Observer<BeforeNews>() {
				@Override public void onCompleted() {
					adapter.notifyDataSetChanged();
				}

				@Override public void onError(Throwable e) {
					TUtils.showShort(getActivity(), "服务器出问题了");
					mProgressBar.setVisibility(View.GONE);
				}

				@Override public void onNext(BeforeNews beforeNews) {
					mProgressBar.setVisibility(View.GONE);
				}
			});
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		outState.putString(TIMEID, id);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 在页面切换时停止活动view
	 */
	@Override public void onHiddenChanged(boolean hidden) {
		if (hidden == true) {
			mProgressBar.setVisibility(View.GONE);
		}
		super.onHiddenChanged(hidden);
	}

	public void onEvent(FabClickEvent fabClickEvent) {
		if (fabClickEvent.getFragmentId() == 6) {
			mListView.smoothScrollToPosition(0);
		}
	}
}
