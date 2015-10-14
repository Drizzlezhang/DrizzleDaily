package com.drizzle.drizzledaily.fragments;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.ui.ThemeListActivity;
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
 * 主题日报列表
 */
public class ThemeListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.theme_grid_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.theme_grid)
    GridView mGridView;

    @Bind(R.id.theme_list_frg_progress)
    ProgressBar mProgressBar;


    private List<BaseListItem> themeItems = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new CommonAdapter<BaseListItem>(getActivity(), themeItems, R.layout.base_grid_item) {
                        @Override
                        public void convert(ViewHolder helper, BaseListItem item) {
                            helper.setText(R.id.grid_item_title, item.getTitle());
                            helper.setImg(R.id.grid_item_img, item.getImgUrl());
                            helper.setText(R.id.grid_item_describe, item.getDescribe());
                        }
                    };
                    mGridView.setAdapter(adapter);
                    mRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    public ThemeListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.theme_list_fragment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        getLists(Config.THEME_LIST);
        return view;
    }

    private void initViews() {
        mRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.black, R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ThemeListActivity.class);
                intent.putExtra("themeid", themeItems.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        getLists(Config.THEME_LIST);
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
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response) {
                try {
                    themeItems.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray others = jsonObject.getJSONArray("others");
                    for (int i = 0; i < others.length(); i++) {
                        JSONObject story = others.getJSONObject(i);
                        int id = story.getInt("id");
                        String title = story.getString("name");
                        String imgUrl = story.getString("thumbnail");
                        String describe = story.getString("description");
                        BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "", describe);
                        themeItems.add(baseListItem);
                    }
                    mProgressBar.setVisibility(View.GONE);
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(getActivity(), "Json数据解析错误");
                    mRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
