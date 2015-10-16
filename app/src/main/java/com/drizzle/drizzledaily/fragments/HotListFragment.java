package com.drizzle.drizzledaily.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 今日热门列表
 */
public class HotListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MainActivity.OnToolbarCilckListener {

    @Bind(R.id.hot_list_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.hot_list)
    ListView mListView;

    private List<BaseListItem> hotListItems = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;
    private static final String HOTCACHENAME = "hotlistcache";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new CommonAdapter<BaseListItem>(getActivity(), hotListItems, R.layout.base_list_item) {
                        @Override
                        public void convert(ViewHolder helper, BaseListItem item) {
                            helper.setText(R.id.base_item_title, item.getTitle());
                            helper.setImg(R.id.base_item_img, item.getImgUrl());
                            helper.setText(R.id.base_item_date, "");
                        }
                    };
                    mListView.setAdapter(adapter);
                    mRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    public HotListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hot_list_fragment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
        String hotcachejson = sharedPreferences.getString(HOTCACHENAME, "");
        if (hotcachejson.equals("")) {
            //TODO
        } else {
            manageHotJson(hotcachejson);
        }
        getLists(Config.Hot_NEWS);
        return view;
    }

    private void initViews() {
        ((MainActivity) getActivity()).setToolbarClick(this);
        mRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.black, R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ReadActivity.class);
                intent.putExtra(Config.READID, hotListItems.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        getLists(Config.Hot_NEWS);
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

    @Override
    public void onClickToolbar() {
        mListView.smoothScrollToPosition(0);
    }

    /**
     * 请求数据并存入list
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
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.CACHE_DATA, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(HOTCACHENAME, response);
                    editor.commit();
                    manageHotJson(response);
                }
            });
        } else {
            TUtils.showShort(getActivity(), "网络未连接");
            swipeRefresh(false);
        }
    }

    /**
     * 处理请求到和或者缓存的最新数据
     */
    private void manageHotJson(String hotJson) {
        try {
            hotListItems.clear();
            JSONObject jsonObject = new JSONObject(hotJson);
            JSONArray recent = jsonObject.getJSONArray("recent");
            for (int i = 0; i < recent.length(); i++) {
                JSONObject story = recent.getJSONObject(i);
                int id = story.getInt("news_id");
                String title = story.getString("title");
                String imgUrl = story.getString("thumbnail");
                BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "");
                hotListItems.add(baseListItem);
            }
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
            TUtils.showShort(getActivity(), "Json数据解析错误");
            mRefreshLayout.setRefreshing(false);
        }
    }
}
