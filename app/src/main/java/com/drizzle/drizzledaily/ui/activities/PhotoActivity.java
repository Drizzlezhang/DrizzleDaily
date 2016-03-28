package com.drizzle.drizzledaily.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.ui.fragments.SettingsFragment;
import com.drizzle.drizzledaily.model.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 查看大图片页面
 */
public class PhotoActivity extends BaseActivity {
    @Bind(R.id.photo_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.photo_img)
    PhotoView mPhotoView;

    private String imgurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        imgurl = intent.getStringExtra(Config.IMAGEURL);
        initViews();
    }

    private void initViews() {
        mToolbar.setTitle("查看图片");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mPhotoView.enable();
        if (imgurl.equals(SettingsFragment.STARTIMGCACHEURL)) {
            File file = new File(Config.START_PHOTO_FOLDER, "startimg.jpg");
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            mPhotoView.setImageBitmap(bitmap);
        } else {
            Glide.with(this).load(imgurl)
                    .centerCrop()
                    .error(R.mipmap.place_img).crossFade()
                    .into(mPhotoView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
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
