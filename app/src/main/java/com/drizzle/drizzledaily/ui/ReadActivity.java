package com.drizzle.drizzledaily.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.widget.NestedScrollView;
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
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.CollectBean;
import com.drizzle.drizzledaily.bean.ShareBean;
import com.drizzle.drizzledaily.db.CollectDB;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.OkHttpClientManager;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.github.mrengineer13.snackbar.SnackBar;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 阅读文章主界面
 */
public class ReadActivity extends MySwipeActivity {

    private int readid;
    private String ImgUrl;
    private String body;
    private String pagetltle;
    private String cssadd;
    private String pageUrl;
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

    @Bind(R.id.read_scroll)
    NestedScrollView mNestedScrollView;
    private DialogPlus dialogPlus;
    private CommonAdapter<ShareBean> adapter;
    private List<ShareBean> shareBeanList = new ArrayList<>();
    private Bitmap shareBitmap;
    private IWXAPI wxApi;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Glide.with(getApplicationContext()).load(ImgUrl)
                            .centerCrop().error(R.mipmap.place_img)
                            .crossFade().into(headImg);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        ButterKnife.bind(this);
        initData();
        initViews();
        if (savedInstanceState != null) {
            readid = savedInstanceState.getInt(Config.READID);
        } else {
            Intent intent = getIntent();
            readid = intent.getIntExtra(Config.READID, -1);
        }
        wxApi = WXAPIFactory.createWXAPI(this, Config.WXAPPID);
        wxApi.registerApp(Config.WXAPPID);
        if (NetUtils.isConnected(ReadActivity.this)) {
            managerReadJson(readid);
        } else {
            TUtils.showShort(ReadActivity.this, "网络未连接");
            mProgressBar.setVisibility(View.GONE);
        }

    }

    private void initData() {
        collectDB = CollectDB.getInstance(this);
        ShareBean bean1 = new ShareBean(R.mipmap.frends, "朋友圈");
        ShareBean bean2 = new ShareBean(R.mipmap.weixin, "微信好友");
        shareBeanList.add(bean1);
        shareBeanList.add(bean2);
        adapter = new CommonAdapter<ShareBean>(this, shareBeanList, R.layout.share_list_item) {
            @Override
            public void convert(ViewHolder helper, ShareBean item) {
                helper.setText(R.id.share_item_text, item.getText());
                helper.setImgByid(R.id.share_item_img, item.getImgId());
            }
        };
        shareBitmap = null;
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNestedScrollView.smoothScrollTo(0, 0);
            }
        });
        readWeb.getSettings().setJavaScriptEnabled(true);
        dialogPlus = DialogPlus.newDialog(ReadActivity.this)
                .setAdapter(adapter)
                .setHeader(R.layout.share_head)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (shareBitmap == null) {
                            //这里替换一张自己工程里的图片资源
                            shareBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.labal_icon);
                        }
                        if (position == 1) {
                            wechatShare(1, pagetltle, pageUrl, shareBitmap); //分享到朋友圈
                        } else if (position == 2) {
                            wechatShare(0, pagetltle, pageUrl, shareBitmap); //分享给微信好友
                        }
                        dialogPlus.dismiss();
                    }
                })
                .setCancelable(true)
                .setPadding(20, 30, 20, 20)
                .create();
    }

    /**
     * 处理readjson数据
     * @param managerReadId
     */
    private void managerReadJson(int managerReadId) {
        OkHttpClientManager.getAsyn(Config.NEWS_BODY + managerReadId, new OkHttpClientManager.StringCallback() {
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
                    pageUrl = jsonObject.getString("share_url");
                    readImgres.setText(jsonObject.getString("image_source"));
                    cssadd = jsonObject.getJSONArray("css").getString(0);
                    //开启一个子线程下载分享图片
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                shareBitmap = Glide.with(getApplicationContext()).load(ImgUrl)
                                        .asBitmap().centerCrop()
                                        .into(100, 100).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
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

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag, String shareTitle, String shareUrl, Bitmap sBitmap) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareTitle;
        msg.description = "来自知乎日报 By Drizzle";
        msg.setThumbImage(sBitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Config.READID, readid);
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
                        .withActionMessage("取消")
                        .withTextColorId(R.color.colorAccent)
                        .withDuration(SnackBar.LONG_SNACK)
                        .show();
                break;
            case R.id.action_share:
                dialogPlus.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
