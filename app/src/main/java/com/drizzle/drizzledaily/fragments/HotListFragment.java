package com.drizzle.drizzledaily.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
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
public class HotListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.hot_list_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.hot_list)
    ListView mListView;

    private List<BaseListItem> hotListItems = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;

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
        getLists(Config.Hot_NEWS);
        return view;
    }

    private void initViews() {
        mRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.black, R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        getLists(Config.Hot_NEWS);
    }

    /**
     * 请求数据并存入list
     *
     * @param listUrl
     */
    private void getLists(final String listUrl) {
        Log.d("get", "list");
        mRefreshLayout.setRefreshing(true);
        OkHttpClientManager.getAsyn(listUrl, new OkHttpClientManager.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                TUtils.showShort(getActivity(), "服务器出问题了");
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response) {
                try {
                    hotListItems.clear();
                    JSONObject jsonObject = new JSONObject(response);
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
        });
    }
}
