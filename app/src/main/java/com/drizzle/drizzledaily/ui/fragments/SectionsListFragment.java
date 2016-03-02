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
import android.widget.GridView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.api.ApiBuilder;
import com.drizzle.drizzledaily.api.MyApi;
import com.drizzle.drizzledaily.api.model.Sections;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.ui.activities.SectionListActivity;
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
 * 专栏列表
 */
public class SectionsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
	@Bind(R.id.sections_grid_refresh) SwipeRefreshLayout mRefreshLayout;

	@Bind(R.id.sections_grid) GridView mGridView;

	private static String SECTIONCACHE = "sectionlistcache";

	private List<BaseListItem> sectionsItems = new ArrayList<>();
	private CommonAdapter<BaseListItem> adapter;

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sections_list_fragment, container, false);
		ButterKnife.bind(this, view);
		initViews();
		//String sectioncachejson = PerferUtils.getString(SECTIONCACHE);
		//if (sectioncachejson.equals("")) {
		//    //TODO
		//} else {
		//    manageSectionList(sectioncachejson);
		//}
		getLists();
		return view;
	}

	private void initViews() {
		mRefreshLayout.setOnRefreshListener(this);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), SectionListActivity.class);
				intent.putExtra("sectionid", sectionsItems.get(position).getId());
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
		adapter = new CommonAdapter<BaseListItem>(getActivity(), sectionsItems, R.layout.base_grid_item) {
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
				for (Sections.DataEntity data : sections.getData()) {
					BaseListItem baseListItem =
						new BaseListItem(data.getId(), data.getName(), data.getThumbnail(), false, "",
							data.getDescription());
					sectionsItems.add(baseListItem);
				}
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
			mGridView.smoothScrollToPosition(0);
		}
	}
}