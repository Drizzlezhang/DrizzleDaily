package com.drizzle.drizzledaily.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.ui.MainActivity;
import com.drizzle.drizzledaily.ui.ReadActivity;
import com.drizzle.drizzledaily.utils.DataUtils;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * 首页列表fragment，包括一个viewpager和一个listview
 */
public class LatestListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MainActivity.OnToolbarCilckListener {
    @Bind(R.id.latest_list_refresh)
    SwipeRefreshLayout mRefreshLayout;

    //自动轮播viewpager
    @Bind(R.id.latest_list)
    ListView mListView;
    private AutoScrollViewPager mViewPager;

    private Calendar mCalendar;
    private List<BaseListItem> baseListItems = new ArrayList<>();
    private List<BaseListItem> headpagerItems = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;
    private FragmentStatePagerAdapter fragmentStatePagerAdapter;
    private static final String LATESTCACHENAME = "latestcache";

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
                            helper.setText(R.id.base_item_date, item.getDate());
                        }
                    };
                    mListView.setAdapter(adapter);
                    final FragmentManager manager = getChildFragmentManager();
                    fragmentStatePagerAdapter = new FragmentStatePagerAdapter(manager) {
                        @Override
                        public Fragment getItem(int position) {
                            BaseListItem baseListItem = headpagerItems.get(position);
                            HeadPagerFragment pagerFragment = HeadPagerFragment.newInstance(baseListItem.getImgUrl(), baseListItem.getTitle(), baseListItem.getId());
                            return pagerFragment;
                        }

                        @Override
                        public int getCount() {
                            return headpagerItems.size();
                        }


                    };
                    mViewPager.setInterval(2000);
                    mViewPager.setStopScrollWhenTouch(true);
                    mViewPager.setAdapter(fragmentStatePagerAdapter);
                    mViewPager.startAutoScroll(3000);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.latest_list_fragment, container, false);
        ButterKnife.bind(this, view);
        View headview = inflater.inflate(R.layout.head_viewpager, null, true);
        mViewPager = (AutoScrollViewPager) headview.findViewById(R.id.head_viewpager);
        mListView.addHeaderView(headview);
        mCalendar = Calendar.getInstance();
        initViews();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
        String latestcache = sharedPreferences.getString(LATESTCACHENAME, "");
        if (latestcache.equals("")) {
            //TODO
        } else {
            manageLatestJson(latestcache);
        }
        getLists(Config.LATEST_NEWS);
        return view;
    }

    @Override
    public void onClickToolbar() {
        mListView.smoothScrollToPosition(0);
    }

    private void initViews() {
        ((MainActivity) getActivity()).setToolbarClick(this);
        mRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.black, R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://静止状态
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            String time = DataUtils.printCalendar(mCalendar);
                            getLists(Config.BEFORE_NEWS + time);
                        }
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
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ReadActivity.class);
                intent.putExtra(Config.READID, baseListItems.get(position - 1).getId());
                startActivity(intent);
            }
        });
        mListView.setDivider(null);
    }

    /**
     * 请求数据并存入list，先判断有没有网，有网就用
     *
     * @param listUrl
     */
    private void getLists(final String listUrl) {
        swipeRefresh(true);
        if (NetUtils.isConnected(getActivity())) {
            OkHttpClientManager.getAsyn(listUrl, new OkHttpClientManager.StringCallback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    TUtils.showShort(getActivity(), "服务器出问题了");
                    mRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onResponse(String response) {
                    String data = "";
                    try {
                        if (listUrl.equals(Config.LATEST_NEWS)) {
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(LATESTCACHENAME, response);
                            editor.commit();
                            manageLatestJson(response);
                        } else {
                            data = DataUtils.printDate(DataUtils.getBeforeDay(mCalendar));
                            mCalendar = DataUtils.getAfterDay(mCalendar);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray stories = jsonObject.getJSONArray("stories");
                            for (int i = 0; i < stories.length(); i++) {
                                JSONObject story = stories.getJSONObject(i);
                                int id = story.getInt("id");
                                String title = story.getString("title");
                                String imgUrl = story.getJSONArray("images").getString(0);
                                BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, data);
                                baseListItems.add(baseListItem);
                            }
                            handler.sendEmptyMessage(1);
                            mCalendar = DataUtils.getBeforeDay(mCalendar);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        TUtils.showShort(getActivity(), "Json数据解析错误");
                        mRefreshLayout.setRefreshing(false);
                    }
                }
            });
        } else {
            if (listUrl.equals(Config.LATEST_NEWS)) {
                TUtils.showShort(getActivity(), "网络未连接");
                swipeRefresh(false);
            } else {
                TUtils.showShort(getActivity(), "网络未连接");
                swipeRefresh(false);
            }
        }

    }

    @Override
    public void onRefresh() {
        getLists(Config.LATEST_NEWS);
    }
    /**
     * 在页面切换时停止活动view
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden==true){
            if (mRefreshLayout.isRefreshing()){
                mRefreshLayout.setRefreshing(false);
            }
        }
        super.onHiddenChanged(hidden);
    }


    /**
     * swiperefresh在主线程中无法消失，需要新开线程
     *
     * @param refresh
     */
    private void swipeRefresh(final boolean refresh) {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (refresh) {
                    mRefreshLayout.setRefreshing(true);
                } else {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 处理请求到和或者缓存的最新数据
     *
     * @param jsonResponse
     */
    private void manageLatestJson(String jsonResponse) {
        try {
            baseListItems.clear();
            headpagerItems.clear();
            String date = DataUtils.printDate(mCalendar);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray stories = jsonObject.getJSONArray("stories");
            for (int i = 0; i < stories.length(); i++) {
                JSONObject story = stories.getJSONObject(i);
                int id = story.getInt("id");
                String title = story.getString("title");
                String imgUrl = story.getJSONArray("images").getString(0);
                BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, date);
                baseListItems.add(baseListItem);
            }
            JSONArray topstories = jsonObject.getJSONArray("top_stories");
            for (int i = 0; i < topstories.length(); i++) {
                JSONObject headstory = topstories.getJSONObject(i);
                int headid = headstory.getInt("id");
                String headtitle = headstory.getString("title");
                String headimgUrl = headstory.getString("image");
                BaseListItem headbaseListItem = new BaseListItem(headid, headtitle, headimgUrl, false, "");
                headpagerItems.add(headbaseListItem);
            }
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
            TUtils.showShort(getActivity(), "json error");
        }
    }
//
//    @Override
//    public void setMenuVisibility(boolean menuVisible) {
//        super.setMenuVisibility(menuVisible);
//        if (this.getView() != null) {
//            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
//        }
//    }
}
