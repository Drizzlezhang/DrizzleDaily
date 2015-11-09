package com.drizzle.drizzledaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
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
 * 专栏列表activity
 */
public class SectionListActivity extends MySwipeActivity {

    private int sectionid;
    private static final String SECTIONID = "sectionid";
    private List<BaseListItem> sectionList = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;

    @Bind(R.id.section_list_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.section_list_listview)
    ListView mListView;

    @Bind(R.id.section_list_progress)
    AVLoadingIndicatorView loadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            sectionid = savedInstanceState.getInt(SECTIONID);
        } else {
            sectionid = getIntent().getIntExtra(SECTIONID, -1);
        }
        initViews();
        if (NetUtils.isConnected(SectionListActivity.this)) {
                new OkHttpRequest.Builder().url(Config.SECTION_LIST_EVERY + sectionid).get(new ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                    TUtils.showShort(SectionListActivity.this, "服务器出问题了");
                    loadingIndicatorView.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String name = jsonObject.getString("name");
                        mToolbar.setTitle(name);
                        JSONArray stories = jsonObject.getJSONArray("stories");
                        for (int i = 0; i < stories.length(); i++) {
                            JSONObject story = stories.getJSONObject(i);
                            int id = story.getInt("id");
                            String title = story.getString("title");
                            String imgUrl = story.getJSONArray("images").getString(0);
                            String date = story.getString("display_date");
                            BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, date);
                            sectionList.add(baseListItem);
                        }
                        handler.sendEmptyMessage(0);
                        loadingIndicatorView.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        TUtils.showShort(SectionListActivity.this, "json error");
                        loadingIndicatorView.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            TUtils.showShort(SectionListActivity.this, "网络未连接");
            loadingIndicatorView.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(0);
            }
        });
        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SectionListActivity.this, ReadActivity.class);
                intent.putExtra(Config.READID, sectionList.get(position).getId());
                startActivity(intent);
            }
        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter = new CommonAdapter<BaseListItem>(getApplicationContext(), sectionList, R.layout.base_list_item) {
                        @Override
                        public void convert(ViewHolder helper, BaseListItem item) {
                            helper.setText(R.id.base_item_title, item.getTitle());
                            helper.setImg(R.id.base_item_img, item.getImgUrl());
                            helper.setText(R.id.base_item_date, item.getDate());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SECTIONID, sectionid);
        super.onSaveInstanceState(outState);
    }
}
