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

    private LatestListFragment latestListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        FragmentManager fragmentManager = getSupportFragmentManager();
        latestListFragment = (LatestListFragment) fragmentManager.findFragmentById(R.id.main_frg_container);
        if (latestListFragment == null) {
            latestListFragment = new LatestListFragment();
            fragmentManager.beginTransaction().replace(R.id.main_frg_container, latestListFragment).commit();
        }

    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("首页");
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

                break;
            case R.id.drawer_menu_hot:

                break;
            case R.id.drawer_menu_theme:

                break;
            case R.id.drawer_menu_section:

                break;
            case R.id.drawer_menu_like:

                break;
            case R.id.drawer_menu_settings:

                break;
            case R.id.drawer_menu_night:

                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
