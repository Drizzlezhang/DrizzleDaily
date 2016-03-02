package com.drizzle.drizzledaily.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.MyUser;
import com.drizzle.drizzledaily.ui.activities.BaseActivity;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChangePasswordActivity extends BaseActivity {
	@Bind(R.id.change_toolbar) Toolbar mToolbar;

	@Bind(R.id.change_touxiang) CircleImageView changeTouxiang;

	@Bind(R.id.change_old_password) MaterialEditText changeOldPassword;

	@Bind(R.id.change_new_password) MaterialEditText changeNewPassword;

	@Bind(R.id.change_new_password_again) MaterialEditText changeNewPasswordAgain;

	@Bind(R.id.change_password) Button changePasswordBtn;

	private String oldPassword, newPassword, newPasswordAgain;
	private int[] touxiangs = new int[] {
		R.mipmap.touxiang1, R.mipmap.touxiang2, R.mipmap.touxiang3, R.mipmap.touxiang4, R.mipmap.touxiang5,
		R.mipmap.touxiang6, R.mipmap.touxiang
	};

	private ProgressDialog progressDialog;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		ButterKnife.bind(this);
		initViews();
	}

	private void initViews() {
		mToolbar.setTitle("修改密码");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
		MyUser myUser = BmobUser.getCurrentUser(ChangePasswordActivity.this, MyUser.class);
		changeTouxiang.setImageResource(touxiangs[myUser.getTouxiangId()]);
	}

	/**
	 * 修改密码方法
	 */
	@OnClick(R.id.change_password) public void changePassword() {
		progressDialog.show();
		progressDialog.dismiss();
		oldPassword = changeOldPassword.getText().toString();
		newPassword = changeNewPassword.getText().toString();
		newPasswordAgain = changeNewPasswordAgain.getText().toString();
		if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPasswordAgain)) {
			TUtils.showShort(ChangePasswordActivity.this, "有密码未填上");
			progressDialog.dismiss();
		} else if (!newPassword.equals(newPasswordAgain)) {
			TUtils.showShort(ChangePasswordActivity.this, "新密码填写不相同");
			progressDialog.dismiss();
		} else if (!NetUtils.isConnected(ChangePasswordActivity.this)) {
			TUtils.showShort(ChangePasswordActivity.this, "网络未连接");
			progressDialog.dismiss();
		} else {
			MyUser.updateCurrentUserPassword(ChangePasswordActivity.this, oldPassword, newPassword,
				new UpdateListener() {

					@Override public void onSuccess() {
						// TODO Auto-generated method stub
						TUtils.showShort(ChangePasswordActivity.this, "密码更新成功");
						progressDialog.dismiss();
						finish();
					}

					@Override public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
						TUtils.showShort(ChangePasswordActivity.this, "密码更新失败");
						progressDialog.dismiss();
					}
				});
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
