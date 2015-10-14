package com.drizzle.drizzledaily.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.fragments.CollectListFragment;
import com.drizzle.drizzledaily.fragments.HotListFragment;
import com.drizzle.drizzledaily.fragments.LatestListFragment;
import com.drizzle.drizzledaily.fragments.SearchFragment;
import com.drizzle.drizzledaily.fragments.SectionsListFragment;
import com.drizzle.drizzledaily.fragments.ThemeListFragment;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.DataUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.drizzle.drizzledaily.utils.ThemeUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面，管理多个列表fragment
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.main_nav_view)
    NavigationView navigationView;

    private Calendar calendar;
    //主activity的fragmentid，默认为首页，1
    private int fragmentID = 1;
    private FragmentManager fragmentManager;
    private LatestListFragment latestListFragment;
    private HotListFragment hotListFragment;
    private ThemeListFragment themeListFragment;
    private SectionsListFragment sectionsListFragment;
    private CollectListFragment collectListFragment;
    private SearchFragment searchFragment;
    private MainActivity instance;

    private IWXAPI wxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
        int themeid = preferences.getInt(Config.SKIN_NUMBER, 0);
        ThemeUtils.onActivityCreateSetTheme(this, themeid);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        wxApi = WXAPIFactory.createWXAPI(this, "wxcdfd8ea3dceaf767");
        wxApi.registerApp("wxcdfd8ea3dceaf767");
        instance = this;
        calendar = Calendar.getInstance();
        fragmentManager = getSupportFragmentManager();
        if (latestListFragment == null) {
            latestListFragment = new LatestListFragment();
            fragmentManager.beginTransaction().replace(R.id.main_frg_container, latestListFragment).commit();
        }

    }

    private void initViews() {
        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private String[] strings = new String[]{"原装色", "火焰红", "冷酷蓝", "高级黑", "热烈橙", "生命绿", "高贵紫", "香蕉黄"};

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_skin:
                new MaterialDialog.Builder(MainActivity.this)
                        .title("换个皮肤吧~")
                        .items(strings)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                instance.finish();
                                SharedPreferences preferences = getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt(Config.SKIN_NUMBER, which);
                                editor.commit();
                                instance.startActivity(new Intent(instance, instance.getClass()));
                                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                            }
                        })
                        .positiveText(android.R.string.cancel)
                        .show();
                break;
            case R.id.action_share:
                wechatShare(0);//分享到微信好友
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "https://github.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "微信分享测试";
        msg.description = "来自Drizzle的应用";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_android);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_menu_home:
                if (latestListFragment == null) {
                    latestListFragment = new LatestListFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.main_frg_container, latestListFragment).commit();
                fragmentID = 1;
                mToolbar.setTitle("知乎日报");
                break;
            case R.id.drawer_menu_hot:
                if (hotListFragment == null) {
                    hotListFragment = new HotListFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.main_frg_container, hotListFragment).commit();
                mToolbar.setTitle("大家都在看");
                fragmentID = 2;
                break;
            case R.id.drawer_menu_theme:
                if (themeListFragment == null) {
                    themeListFragment = new ThemeListFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.main_frg_container, themeListFragment).commit();
                mToolbar.setTitle("主题日报");
                fragmentID = 3;
                break;
            case R.id.drawer_menu_section:
                if (sectionsListFragment == null) {
                    sectionsListFragment = new SectionsListFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.main_frg_container, sectionsListFragment).commit();
                mToolbar.setTitle("专栏");
                fragmentID = 4;
                break;
            case R.id.drawer_menu_like:
                if (collectListFragment == null) {
                    collectListFragment = new CollectListFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.main_frg_container, collectListFragment).commit();
                mToolbar.setTitle("我的收藏");
                fragmentID = 5;
                break;
            case R.id.drawer_menu_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.drawer_menu_choose:
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.drawer_menu_drizzle:
                //在需要分享的地方添加代码：

                wechatShare(1);//分享到微信朋友圈
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 根据选择的日期获取当天的日报内容
     */
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = DataUtils.setCalendar(year, monthOfYear + 1, dayOfMonth);
            String titleTime = DataUtils.printDate(c);
            c = DataUtils.getAfterDay(c);
            String time = DataUtils.printCalendar(c);
            searchFragment = SearchFragment.newInstance(time);
            fragmentManager.beginTransaction().replace(R.id.main_frg_container, searchFragment).commit();
            mToolbar.setTitle(titleTime);
            fragmentID = 6;
        }
    };

    private static boolean isExit = false;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (!isExit) {
            isExit = true;
            TUtils.showShort(MainActivity.this, "再按一次退出~");
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
}
