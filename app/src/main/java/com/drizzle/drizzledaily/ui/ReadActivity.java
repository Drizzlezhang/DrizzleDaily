package com.drizzle.drizzledaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Glide.with(ReadActivity.this)
                            .load(ImgUrl)
                            .centerCrop()
                            .error(R.mipmap.default_pic)
                            .crossFade()
                            .into(headImg);
                    String css = "<link rel=\"stylesheet\" href=\""+ cssadd + "type=\"text/css\">";
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        ButterKnife.bind(this);
        initViews();
        collectDB=CollectDB.getInstance(this);
        Intent intent = getIntent();
        readid = intent.getIntExtra("readid", -1);
        OkHttpClientManager.getAsyn(Config.NEWS_BODY + readid, new OkHttpClientManager.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                TUtils.showShort(ReadActivity.this, "服务器出问题了");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("title");
                    readTitle.setText(name);
                    pagetltle=name;
                    ImgUrl= jsonObject.getString("image");
                    body=jsonObject.getString("body");
                    readImgres.setText(jsonObject.getString("image_source"));
                    cssadd=jsonObject.getJSONArray("css").getString(0);
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    TUtils.showShort(ReadActivity.this, "json error");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                finish();
                break;
            case R.id.action_collect:
                new MaterialDialog.Builder(ReadActivity.this)
                        .title("收藏")
                        .content("将这篇文章添加到本地收藏夹。")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                CollectBean bean=new CollectBean(readid,pagetltle,1);
                                collectDB.saveCollect(bean);
                                TUtils.showShort(ReadActivity.this,"已收藏");
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
