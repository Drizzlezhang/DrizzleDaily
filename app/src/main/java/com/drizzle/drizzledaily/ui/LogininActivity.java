package com.drizzle.drizzledaily.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.MyUser;
import com.drizzle.drizzledaily.ui.activities.BaseActivity;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 登录界面
 */
public class LogininActivity extends BaseActivity {
	@Bind(R.id.loginin_toolbar) Toolbar mToolbar;

	@Bind(R.id.loginin_input_name) MaterialEditText loginInputName;

	@Bind(R.id.loginin_input_password) MaterialEditText loginInputPassword;

	@Bind(R.id.loginin_register_btn) Button registerBtn;

	@Bind(R.id.loginin_btn) Button logininBtn;

	@Bind(R.id.loginin_touxiang) CircleImageView touxiang;

	private ProgressDialog progressDialog;

	public static LogininActivity instance;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginin);
		ButterKnife.bind(this);
		initViews();
		instance = this;
	}

	private void initViews() {
		mToolbar.setTitle("登录");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
	}

	@OnClick({ R.id.loginin_btn, R.id.loginin_register_btn }) public void btn(View view) {
		switch (view.getId()) {
			case R.id.loginin_btn:
				progressDialog.show();
				String name = loginInputName.getText().toString();
				String password = loginInputPassword.getText().toString();
				if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
					TUtils.showShort(LogininActivity.this, "用户名或密码未完善");
					progressDialog.dismiss();
				} else if (!NetUtils.isConnected(LogininActivity.this)) {
					TUtils.showShort(LogininActivity.this, "网络未连接");
					progressDialog.dismiss();
				} else {
					MyUser user = new MyUser();
					user.setUsername(name);
					user.setPassword(password);
					user.login(LogininActivity.this, new SaveListener() {
						@Override public void onSuccess() {
							TUtils.showShort(LogininActivity.this, "登录成功");
							progressDialog.dismiss();
							finish();
						}

						@Override public void onFailure(int i, String s) {
							TUtils.showShort(LogininActivity.this, "登录失败");
							progressDialog.dismiss();
						}
					});
				}
				break;
			case R.id.loginin_register_btn:
				startActivity(new Intent(LogininActivity.this, RegisterActivity.class));
				break;
			default:
				break;
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
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
