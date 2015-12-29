package com.drizzle.drizzledaily.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.adapter.CommonAdapter;
import com.drizzle.drizzledaily.adapter.ViewHolder;
import com.drizzle.drizzledaily.bean.MyUser;
import com.drizzle.drizzledaily.bean.ShareBean;
import com.drizzle.drizzledaily.ui.activities.BaseActivity;
import com.drizzle.drizzledaily.utils.NetUtils;
import com.drizzle.drizzledaily.utils.TUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 注册界面
 */
public class RegisterActivity extends BaseActivity {
	@Bind(R.id.register_toolbar) Toolbar mToolbar;

	@Bind(R.id.register_touxiang) CircleImageView registerTouxiang;

	@Bind(R.id.register_btn) Button registerBtn;

	@Bind(R.id.register_input_name) MaterialEditText registerName;

	@Bind(R.id.register_input_password) MaterialEditText registerPassword;

	@Bind(R.id.register_input_password_again) MaterialEditText registerPasswordAgain;

	@Bind(R.id.register_sex) RadioGroup registerSex;

	private int sex = 0;
	private int touxiangid = 6;
	private String name, password, passwordAgain;
	private CommonAdapter<ShareBean> adapter;
	private List<ShareBean> touxiangList = new ArrayList<>();
	private int[] touxiangs = new int[] {
		R.mipmap.touxiang1, R.mipmap.touxiang2, R.mipmap.touxiang3, R.mipmap.touxiang4, R.mipmap.touxiang5,
		R.mipmap.touxiang6
	};
	private String[] superheros = new String[] { "SpiderMan", "IronMan", "Hulk", "SuperMan", "GreenArrow", "BatMan" };

	private DialogPlus dialogPlus;
	private ProgressDialog progressDialog;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ButterKnife.bind(this);
		initDatas();
		initViews();
	}

	private void initViews() {
		mToolbar.setTitle("注册");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		registerSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override public void onCheckedChanged(RadioGroup group, int checkedId) {
				sex = checkedId;
			}
		});
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
		dialogPlus = DialogPlus.newDialog(RegisterActivity.this)
			.setAdapter(adapter)
			.setGravity(Gravity.CENTER)
			.setContentHolder(new GridHolder(3))
			.setOnBackPressListener(new OnBackPressListener() {
				@Override public void onBackPressed(DialogPlus dialogPlus) {
					dialogPlus.dismiss();
				}
			})
			.setOnItemClickListener(new OnItemClickListener() {
				@Override public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
					registerTouxiang.setImageResource(touxiangs[position]);
					touxiangid = position;
					dialogPlus.dismiss();
				}
			})
			.setCancelable(true)
			.setPadding(20, 20, 20, 20)
			.create();
		registerTouxiang.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				dialogPlus.show();
			}
		});
	}

	private void initDatas() {
		for (int i = 0; i < 6; i++) {
			touxiangList.add(new ShareBean(touxiangs[i], superheros[i]));
		}
		adapter = new CommonAdapter<ShareBean>(this, touxiangList, R.layout.choose_touxiang_item) {
			@Override public void convert(ViewHolder helper, ShareBean item) {
				helper.setText(R.id.choose_superhero, item.getText());
				helper.setImgByid(R.id.choose_touxiang, item.getImgId());
			}
		};
	}

	@OnClick(R.id.register_btn) public void register() {
		name = registerName.getText().toString();
		password = registerPassword.getText().toString();
		passwordAgain = registerPasswordAgain.getText().toString();
		if (touxiangid == 6) {
			new MaterialDialog.Builder(RegisterActivity.this).title("不换个头像么？")
				.content("点击头像更换。")
				.positiveText("前去更换")
				.negativeText("继续注册")
				.callback(new MaterialDialog.ButtonCallback() {
					@Override public void onPositive(MaterialDialog dialog) {
						dialogPlus.show();
					}

					@Override public void onNegative(MaterialDialog dialog) {
						registerUser();
					}
				})
				.show();
		} else {
			registerUser();
		}
	}

	/**
	 * 注册方法
	 */
	private void registerUser() {
		progressDialog.show();
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordAgain) || (sex == 0)) {
			TUtils.showShort(RegisterActivity.this, "信息填写未完善");
			progressDialog.dismiss();
		} else if (!passwordAgain.equals(password)) {
			TUtils.showShort(RegisterActivity.this, "密码未填写正确");
			progressDialog.dismiss();
		} else if (!NetUtils.isConnected(RegisterActivity.this)) {
			TUtils.showShort(RegisterActivity.this, "网络未连接");
			progressDialog.dismiss();
		} else {
			MyUser user = new MyUser();
			user.setSex(sex);
			user.setTouxiangId(touxiangid);
			user.setUsername(name);
			user.setPassword(password);
			user.signUp(this, new SaveListener() {
				@Override public void onSuccess() {
					LogininActivity.instance.finish();
					TUtils.showShort(RegisterActivity.this, "注册成功");
					finish();
				}

				@Override public void onFailure(int i, String s) {
					TUtils.showShort(RegisterActivity.this, "注册失败");
				}
			});
			progressDialog.dismiss();
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
