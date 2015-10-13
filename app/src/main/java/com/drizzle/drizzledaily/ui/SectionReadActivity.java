package com.drizzle.drizzledaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.utils.TUtils;
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

    @Bind(R.id.section_read_title)
    TextView sectionTitle;

    @Bind(R.id.section_read_webview)
    WebView sectionWeb;

    @Bind(R.id.from_section_btn)
    Button fromSection;

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
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("title");
                    sectionTitle.setText(name);
                    pagetitle = name;
                    body = jsonObject.getString("body");
                    JSONObject theme = jsonObject.getJSONObject("theme");
                    themeid = theme.getInt("id");
                    themename = theme.getString("name");
                    fromSection.setText("来自：" + themename);
                    cssadd=jsonObject.getJSONArray("css").getString(0);
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(SectionReadActivity.this, "json error");
                }
            }
        });
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        sectionWeb.getSettings().setJavaScriptEnabled(true);
        fromSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SectionReadActivity.this, ThemeListActivity.class);
                intent.putExtra("themeid", themeid);
                startActivity(intent);
            }
        });
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
                new MaterialDialog.Builder(SectionReadActivity.this)
                        .title("收藏")
                        .content("将这篇文章添加到本地收藏夹。")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                CollectBean bean = new CollectBean(readid, pagetitle, 2);
                                collectDB.saveCollect(bean);
                                TUtils.showShort(SectionReadActivity.this, "已收藏");
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
