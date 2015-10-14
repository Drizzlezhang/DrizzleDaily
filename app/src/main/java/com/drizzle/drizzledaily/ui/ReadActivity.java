package com.drizzle.drizzledaily.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
 * 阅读文章主界面
 */
public class ReadActivity extends AppCompatActivity {

    private int readid;
    private String ImgUrl;
    private String body;
    private String pagetltle;
    private String cssadd;
    private CollectDB collectDB;

    @Bind(R.id.read_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.read_headimg)
    ImageView headImg;

    @Bind(R.id.read_title)
    TextView readTitle;

    @Bind(R.id.read_imgres)
    TextView readImgres;

    @Bind(R.id.read_webview)
    WebView readWeb;

    @Bind(R.id.read_progress)
    ProgressBar mProgressBar;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Glide.with(getApplicationContext())
                            .load(ImgUrl)
                            .centerCrop()
                            .error(R.mipmap.default_pic)
                            .crossFade()
                            .into(headImg);
                    String css = "<link rel=\"stylesheet\" href=\"" + cssadd + "type=\"text/css\">";
                    String html = "<html><head>" + css + "</head><body>" + body + "</body></html>";
                    html = html.replace("<div class=\"img-place-holder\">", "");
                    readWeb.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
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
        setContentView(R.layout.activity_read);
        ButterKnife.bind(this);
        initViews();
        collectDB = CollectDB.getInstance(this);
        if (savedInstanceState!=null){
            readid=savedInstanceState.getInt("readid");
        }else{
            Intent intent = getIntent();
            readid = intent.getIntExtra("readid", -1);
        }
        OkHttpClientManager.getAsyn(Config.NEWS_BODY + readid, new OkHttpClientManager.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                TUtils.showShort(ReadActivity.this, "服务器出问题了");
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("title");
                    readTitle.setText(name);
                    pagetltle = name;
                    ImgUrl = jsonObject.getString("image");
                    body = jsonObject.getString("body");
                    readImgres.setText(jsonObject.getString("image_source"));
                    cssadd = jsonObject.getJSONArray("css").getString(0);
                    handler.sendEmptyMessage(0);
                    mProgressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(ReadActivity.this, "json error");
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        readWeb.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("readid",readid);
        super.onSaveInstanceState(outState);
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
                CollectBean bean = new CollectBean(readid, pagetltle, 1);
                collectDB.deleteCollect(readid);
                collectDB.saveCollect(bean);
                new SnackBar.Builder(this)
                        .withOnClickListener(new SnackBar.OnMessageClickListener() {
                            @Override
                            public void onMessageClick(Parcelable token) {
                                collectDB.deleteCollect(readid);
                                TUtils.showShort(ReadActivity.this, "已取消收藏");
                            }
                        })
                        .withMessage("已收藏到本地文件夹。")
                        .withActionMessage("取消") // OR
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
