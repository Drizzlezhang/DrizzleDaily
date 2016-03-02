package com.drizzle.drizzledaily.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.BugFeedBack;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.activities.PhotoActivity;
import com.drizzle.drizzledaily.utils.TUtils;

import cn.bmob.v3.listener.SaveListener;

/**
 * 设置fragment
 */
public class SettingsFragment extends PreferenceFragment {
	private String[] strings;
	public static final String STARTIMGCACHEURL = Config.START_PHOTO_FOLDER + "/startimg.jpg";
	private ProgressDialog progressDialog;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		strings = new String[] { "原装色", "火焰红", "冷酷蓝", "高级黑", "热烈橙", "生命绿", "高贵紫", "香蕉黄" };
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("请稍等...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
	}

	@Override public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {
		switch (preference.getKey()) {
			case "seestartimg":
				Intent intent = new Intent(getActivity(), PhotoActivity.class);
				intent.putExtra(Config.IMAGEURL, STARTIMGCACHEURL);
				startActivity(intent);
				break;
			//提交bug
			case "bugfeedback":
				final String model = Build.MODEL;
				final String company = Build.MANUFACTURER;
				new MaterialDialog.Builder(getActivity()).title("快速反馈应用bug")
					.inputType(InputType.TYPE_CLASS_TEXT)
					.positiveText("发送")
					.input("输入内容", "", new MaterialDialog.InputCallback() {
						@Override public void onInput(MaterialDialog dialog, CharSequence input) {
							if (TextUtils.isEmpty(input)) {
								TUtils.showShort(getActivity(), "内容不能为空");
							} else {
								progressDialog.show();
								BugFeedBack feedBack = new BugFeedBack();
								feedBack.setFeedBackContents(input.toString());
								feedBack.setModel(
									model + " from " + company);//获取到手机型号和品牌 TUtils.showShort(getActivity(),"bug提交成功");
								feedBack.save(getActivity(), new SaveListener() {
									@Override public void onSuccess() {
										TUtils.showShort(getActivity(), "bug提交成功");
										progressDialog.dismiss();
									}

									@Override public void onFailure(int i, String s) {
										TUtils.showShort(getActivity(), "bug提交失败");
										progressDialog.dismiss();
									}
								});
							}
						}
					})
					.show();
				break;
			case "clearcache":
				new Thread(new Runnable() {
					@Override public void run() {
						Glide.get(getActivity()).clearDiskCache();
					}
				}).start();
				Glide.get(getActivity()).clearMemory();
				progressDialog.show();
				handler.sendEmptyMessageDelayed(1, 1000);
				break;
			default:
				break;
		}
		return true;
	}

	private Handler handler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					TUtils.showShort(getActivity(), "缓存已清理");
					progressDialog.dismiss();
					break;
				default:
					break;
			}
		}
	};
}
