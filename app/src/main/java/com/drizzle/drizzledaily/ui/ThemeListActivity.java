package com.drizzle.drizzledaily.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.BaseListItem;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.utils.TUtils;
import com.drizzle.drizzledaily.utils.ThemeUtils;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
/*
主题日报列表activity
 */
public class ThemeListActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Bind(R.id.theme_list)
    ListView mListView;

    @Bind(R.id.theme_list_headimg)
    ImageView mImageView;

    @Bind(R.id.theme_list_title)
    TextView mTextView;

    @Bind(R.id.theme_list_des)
    TextView themeDes;

    @Bind(R.id.theme_list_progress)
    ProgressBar mProgressBar;

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
                    adapter = new CommonAdapter<BaseListItem>(ThemeListActivity.this, themeList, R.layout.simple_list_item) {
                        @Override
                        public void convert(ViewHolder helper, BaseListItem item) {
                            helper.setText(R.id.simple_item_title, item.getTitle());
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
        SharedPreferences preferences=getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
        int themeid=preferences.getInt(Config.SKIN_NUMBER,0);
        ThemeUtils.onActivityCreateSetTheme(this, themeid);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_theme);
        ButterKnife.bind(this);
        initViews();
        Intent intent = getIntent();
        themeId = intent.getIntExtra("themeid", -1);
        OkHttpClientManager.getAsyn(Config.THEME_LIST_EVERY + themeId, new OkHttpClientManager.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                TUtils.showShort(ThemeListActivity.this, "服务器出问题了");
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("name");
                    mTextView.setText(name);
                    imgUrl = jsonObject.getString("background");
                    themeDes.setText(jsonObject.getString("description"));
                    JSONArray stories = jsonObject.getJSONArray("stories");
                    for (int i = 0; i < stories.length(); i++) {
                        JSONObject story = stories.getJSONObject(i);
                        int id = story.getInt("id");
                        String title = story.getString("title");
                        BaseListItem baseListItem = new BaseListItem(id, title, imgUrl, false, "");
                        themeList.add(baseListItem);
                    }
                    handler.sendEmptyMessage(0);
                    mProgressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(ThemeListActivity.this, "json error");
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.theme_list_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ThemeListActivity.this, SectionReadActivity.class);
                intent.putExtra("readid", themeList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    /**
     * 测量listview高度，解决和scrollview冲突问题
     *
     * @param listView
     */
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

}
