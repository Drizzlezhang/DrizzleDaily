package com.drizzle.drizzledaily.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.drizzle.drizzledaily.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 登录界面
 */
public class LogininActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.loginin_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.loginin_input_name)
    MaterialEditText loginInputName;

    @Bind(R.id.loginin_input_password)
    MaterialEditText loginInputPassword;

    @Bind(R.id.loginin_register_btn)
    Button registerBtn;

    @Bind(R.id.loginin_btn)
    Button logininBtn;

    @Bind(R.id.loginin_touxiang)
    CircleImageView touxiang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginin);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        mToolbar.setTitle("登录");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginin_btn:

                break;
            case R.id.loginin_register_btn:

                break;
            default:
                break;
        }
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
