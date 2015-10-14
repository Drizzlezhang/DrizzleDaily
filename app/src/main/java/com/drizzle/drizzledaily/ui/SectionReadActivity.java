package com.drizzle.drizzledaily.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.utils.TUtils;
import com.drizzle.drizzledaily.utils.ThemeUtils;
import com.github.mrengineer13.snackbar.SnackBar;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专栏日报阅读界面（没有大图提供,单开一个页面）
 */

public class SectionReadActivity extends AppCompatActivity {
    @Bind(R.id.section_read_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.section_read_webview)
    WebView sectionWeb;

    @Bind(R.id.section_read_progress)
    ProgressBar mProgressBar;

    private String body;
    private int readid;
    private int themeid;
    private String themename;
    private String pagetitle;
    private CollectDB collectDB;
    private String cssadd;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String css = "<link rel=\"stylesheet\" href=\""+ cssadd + "type=\"text/css\">";
                    String html = "<html><head>" + css + "</head><body>" + body + "</body></html>";
                    html = html.replace("<div class=\"img-place-holder\">", "");
                    sectionWeb.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences=getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
        int thid=preferences.getInt(Config.SKIN_NUMBER,0);
        ThemeUtils.onActivityCreateSetTheme(this, thid);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_read);
        ButterKnife.bind(this);
        initViews();
        collectDB = CollectDB.getInstance(this);
        Intent intent = getIntent();
        readid = intent.getIntExtra("readid", -1);
        OkHttpClientManager.getAsyn(Config.NEWS_BODY + readid, new OkHttpClientManager.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                TUtils.showShort(SectionReadActivity.this, "服务器出问题了");
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("title");
                    mToolbar.setTitle(name);
                    pagetitle = name;
                    body = jsonObject.getString("body");
                    JSONObject theme = jsonObject.getJSONObject("theme");
                    themeid = theme.getInt("id");
                    themename = theme.getString("name");
                    cssadd=jsonObject.getJSONArray("css").getString(0);
                    handler.sendEmptyMessage(0);
                    mProgressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(SectionReadActivity.this, "json error");
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {
        mToolbar.setTitle("载入中");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        sectionWeb.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:

                finish();
                break;
            case R.id.action_collect:
                CollectBean bean = new CollectBean(readid, pagetitle, 2);
                collectDB.deleteCollect(readid);
                collectDB.saveCollect(bean);
                new SnackBar.Builder(this)
                        .withOnClickListener(new SnackBar.OnMessageClickListener() {
                            @Override
                            public void onMessageClick(Parcelable token) {
                                collectDB.deleteCollect(readid);
                                TUtils.showShort(SectionReadActivity.this, "已取消收藏");
                            }
                        })
                        .withMessage("已收藏到本地文件夹。")
                        .withActionMessage("取消")
                        .withTextColorId(R.color.colorAccent)
                        .withDuration(SnackBar.LONG_SNACK)
                        .show();
                break;
            case R.id.action_share:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
