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
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Created by user on 2015/10/12.
 */
public class SectionsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.sections_grid_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.sections_grid)
    GridView mGridView;

    private List<BaseListItem> sectionsItems = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new CommonAdapter<BaseListItem>(getActivity(), sectionsItems, R.layout.base_grid_item) {
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

    public SectionsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sections_list_fragment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        getLists(Config.SECTION_LIST);
        return view;
    }

    private void initViews() {
        mRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.black, R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TUtils.showShort(getActivity(), sectionsItems.get(position).getDescribe());
            }
        });
    }


    @Override
    public void onRefresh() {
        getLists(Config.SECTION_LIST);
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
                    sectionsItems.clear();
                    JSONObject jsonObject = new JSONObject(response);
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
        });
    }
}

