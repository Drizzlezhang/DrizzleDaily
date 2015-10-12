package com.drizzle.drizzledaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.utils.ImgUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.squareup.okhttp.Request;

import org.apache.http.conn.scheme.HostNameResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ThemeListActivity extends AppCompatActivity {
    @Bind(R.id.theme_list_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.theme_list)
    ListView mListView;

    @Bind(R.id.theme_list_headimg)
    ImageView mImageView;

    private int themeId;
    private String imgUrl;
    private List<BaseListItem> themeList = new ArrayList<>();
    private CommonAdapter<BaseListItem> adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Glide.with(ThemeListActivity.this)
                            .load(imgUrl)
                            .centerCrop()
                            .error(R.mipmap.default_pic)
                            .crossFade()
                            .into(mImageView);
                    adapter = new CommonAdapter<BaseListItem>(ThemeListActivity.this, themeList, R.layout.base_list_item) {
                        @Override
                        public void convert(ViewHolder helper, BaseListItem item) {
                            helper.setText(R.id.base_item_title, item.getTitle());
                            helper.setImg(R.id.base_item_img, item.getImgUrl());
                        }
                    };
                    mListView.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(mListView);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_theme);
        ButterKnife.bind(this);
        initViews();
        Intent intent = getIntent();
        themeId = intent.getIntExtra("themeid", -1);
        Log.d("url",Config.THEME_LIST_EVERY + themeId);
        OkHttpClientManager.getAsyn(Config.THEME_LIST_EVERY + themeId, new OkHttpClientManager.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                TUtils.showShort(ThemeListActivity.this, "服务器出问题了");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    mToolbar.setTitle(jsonObject.getString("name"));
                    imgUrl = jsonObject.getString("background");
                    JSONArray stories = jsonObject.getJSONArray("stories");
                    for (int i = 0; i < stories.length(); i++) {
                        JSONObject story = stories.getJSONObject(i);
                        int id = story.getInt("id");
                        String title = story.getString("title");
                        BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "");
                        themeList.add(baseListItem);
                    }
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(ThemeListActivity.this, "json error");
                }
            }
        });
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
