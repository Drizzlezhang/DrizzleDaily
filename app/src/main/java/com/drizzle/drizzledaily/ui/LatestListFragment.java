package com.drizzle.drizzledaily.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.utils.DataUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;

import org.apache.http.conn.scheme.HostNameResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by drizzle on 2015/10/10.
 */
public class LatestListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.latest_list_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.latest_list)
    ListView mListView;

    private Calendar mCalendar;
    private List<BaseListItem> baseListItems = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;

    public LatestListFragment() {
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new CommonAdapter<BaseListItem>(getActivity(), baseListItems, R.layout.base_list_item) {
                        @Override
                        public void convert(ViewHolder helper, BaseListItem item) {
                            helper.setText(R.id.base_item_title, item.getTitle());
                            helper.setImg(R.id.base_item_img, item.getImgUrl());
                        }
                    };
                    mListView.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(mListView);
                    mRefreshLayout.setRefreshing(false);
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.latest_list_frg, container, false);
        ButterKnife.bind(this, view);
        initViews();
        getLists(Config.LATEST_NEWS);
        return view;
    }

    private void initViews() {
        mRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.black, R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        String time = DataUtils.printCalendar(mCalendar);
                        getLists(Config.BEFORE_NEWS + time);
                        TUtils.showShort(getActivity(), time);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void getLists(final String listUrl) {
        mRefreshLayout.setRefreshing(true);
        OkHttpClientManager.getAsyn(listUrl, new OkHttpClientManager.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                TUtils.showShort(getActivity(), "服务器出问题了");
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response) {
                if (listUrl.equals(Config.LATEST_NEWS)) {
                    baseListItems.clear();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray stories = jsonObject.getJSONArray("stories");
                    for (int i = 0; i < stories.length(); i++) {
                        JSONObject story = stories.getJSONObject(i);
                        int id = story.getInt("id");
                        String title = story.getString("title");
                        String imgUrl = story.getJSONArray("images").getString(0);
                        BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, false);
                        baseListItems.add(baseListItem);
                    }
                    if (listUrl.equals(Config.LATEST_NEWS)) {
                        handler.sendEmptyMessage(0);
                        mCalendar = Calendar.getInstance();
                    } else {
                        handler.sendEmptyMessage(1);
                        mCalendar = DataUtils.getBeforeDay(mCalendar);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(getActivity(), "服务器出问题了");
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        getLists(Config.LATEST_NEWS);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}
