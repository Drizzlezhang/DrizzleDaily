package com.drizzle.drizzledaily.ui.fragments;

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
import android.widget.GridView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.MainActivity;
import com.drizzle.drizzledaily.ui.activities.SectionListActivity;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.PerferUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.OkHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专栏列表
 */
public class SectionsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, MainActivity.OnToolbarCilckListener {
    @Bind(R.id.sections_grid_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.sections_grid)
    GridView mGridView;

    private static String SECTIONCACHE = "sectionlistcache";


    private List<BaseListItem> sectionsItems = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sections_list_fragment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        String sectioncachejson = PerferUtils.getString(SECTIONCACHE);
        if (sectioncachejson.equals("")) {
            //TODO
        } else {
            manageSectionList(sectioncachejson);
        }
        getLists(Config.SECTION_LIST);
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClickToolbar() {
        mGridView.smoothScrollToPosition(0);
    }

    private void initViews() {
        ((MainActivity) getActivity()).setToolbarClick(this);
        mRefreshLayout.setOnRefreshListener(this);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SectionListActivity.class);
                intent.putExtra("sectionid", sectionsItems.get(position).getId());
                startActivity(intent);
            }
        });
        adapter = new CommonAdapter<BaseListItem>(getActivity(), sectionsItems, R.layout.base_grid_item) {
            @Override
            public void convert(ViewHolder helper, BaseListItem item) {
                helper.setText(R.id.grid_item_title, item.getTitle());
                helper.setImg(R.id.grid_item_img, item.getImgUrl());
                helper.setText(R.id.grid_item_describe, item.getDescribe());
            }
        };
        mGridView.setAdapter(adapter);
    }


    @Override
    public void onRefresh() {
        getLists(Config.SECTION_LIST);
    }

    /**
     * 在页面切换时停止活动view
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden == true) {
            if (mRefreshLayout.isRefreshing()) {
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
     *
     * @param listUrl
     */
    private void getLists(final String listUrl) {
        swipeRefresh(true);
        if (NetUtils.isConnected(getActivity())) {
                OkHttpUtils.get().url(listUrl).build().execute(new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    TUtils.showShort(getActivity(), "服务器出问题了");
                    mRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onResponse(String response) {
                    PerferUtils.saveSth(SECTIONCACHE,response);
                    manageSectionList(response);
                }
            });
        } else {
            swipeRefresh(false);
            TUtils.showShort(getActivity(), "网络未连接");
        }
    }

    private void manageSectionList(String sectionsJson) {
        try {
            sectionsItems.clear();
            JSONObject jsonObject = new JSONObject(sectionsJson);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject story = data.getJSONObject(i);
                int id = story.getInt("id");
                String title = story.getString("name");
                String imgUrl = story.getString("thumbnail");
                String describe = story.getString("description");
                BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "", describe);
                sectionsItems.add(baseListItem);
            }
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
            TUtils.showShort(getActivity(), "Json数据解析错误");
            mRefreshLayout.setRefreshing(false);
        }
    }
}

