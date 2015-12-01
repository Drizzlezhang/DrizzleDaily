package com.drizzle.drizzledaily.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.drizzle.drizzledaily.ui.MainActivity;
import com.drizzle.drizzledaily.ui.ReadActivity;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.http.okhttp.callback.ResultCallback;
import com.zhy.http.okhttp.request.OkHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用于显示根据日期查找到的日报数据
 */
public class SearchFragment extends android.support.v4.app.Fragment implements MainActivity.OnToolbarCilckListener {

    @Bind(R.id.search_list)
    ListView mListView;

    @Bind(R.id.search_progress)
    AVLoadingIndicatorView loadingIndicatorView;

    private CommonAdapter<BaseListItem> adapter;
    private String id;
    private List<BaseListItem> baseListItems = new ArrayList<>();
    private static final String TIMEID = "timeid";

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String timeid) {
        Bundle args = new Bundle();
        args.putString(TIMEID, timeid);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            id = savedInstanceState.getString(TIMEID);
        } else {
            id = getArguments().getString(TIMEID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).setToolbarClick(this);
        if (NetUtils.isConnected(getActivity())) {
            new OkHttpRequest.Builder().url(Config.BEFORE_NEWS + id).get(new ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {
                    TUtils.showShort(getActivity(), "服务器出问题了");
                    loadingIndicatorView.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray stories = jsonObject.getJSONArray("stories");
                        for (int i = 0; i < stories.length(); i++) {
                            JSONObject story = stories.getJSONObject(i);
                            int id = story.getInt("id");
                            String title = story.getString("title");
                            String imgUrl = story.getJSONArray("images").getString(0);
                            BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "");
                            baseListItems.add(baseListItem);
                        }
                        loadingIndicatorView.setVisibility(View.GONE);
                        handler.sendEmptyMessage(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        TUtils.showShort(getActivity(), "json error");
                        loadingIndicatorView.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            TUtils.showShort(getActivity(), "网络未连接");
            loadingIndicatorView.setVisibility(View.GONE);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ReadActivity.class);
                intent.putExtra(Config.READID, baseListItems.get(position).getId());
                startActivity(intent);
            }
        });
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter = new CommonAdapter<BaseListItem>(getActivity(), baseListItems, R.layout.base_list_item) {
                        @Override
                        public void convert(ViewHolder helper, BaseListItem item) {
                            helper.setText(R.id.base_item_title, item.getTitle());
                            helper.setImg(R.id.base_item_img, item.getImgUrl());
                        }
                    };
                    mListView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TIMEID, id);
        super.onSaveInstanceState(outState);
    }

    /**
     * 在页面切换时停止活动view
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden == true) {
            loadingIndicatorView.setVisibility(View.GONE);
        }
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onClickToolbar() {
        mListView.smoothScrollToPosition(0);
    }
}
