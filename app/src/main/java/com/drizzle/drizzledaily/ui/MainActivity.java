package com.drizzle.drizzledaily.ui;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.fragments.HotListFragment;
import com.drizzle.drizzledaily.fragments.LatestListFragment;
import com.drizzle.drizzledaily.fragments.SectionsListFragment;
import com.drizzle.drizzledaily.fragments.ThemeListFragment;

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
    //主activity的fragmentid，默认为首页，1
    private int fragmentID = 1;
    private FragmentManager fragmentManager;

    private LatestListFragment latestListFragment;
    private HotListFragment hotListFragment;
    private ThemeListFragment themeListFragment;
    private SectionsListFragment sectionsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
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
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                mToolbar.setTitle("首页");
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

                break;
            case R.id.drawer_menu_settings:

                break;
            case R.id.drawer_menu_night:

                break;
            case R.id.drawer_menu_drizzle:

                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
