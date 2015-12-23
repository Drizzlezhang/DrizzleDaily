package com.drizzle.drizzledaily.fragments;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.utils.TUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import im.fir.sdk.FIR;
import im.fir.sdk.callback.VersionCheckCallback;
import im.fir.sdk.version.AppVersion;

/**
 * 关于开发者页面，意见反馈
 */
public class AboutDeveloperFragment extends android.support.v4.app.Fragment {
    @Bind(R.id.about_dev_versionname)
    TextView devVersion;

    @Bind(R.id.suggestion_btn)
    Button suggestionBtn;

    @Bind(R.id.check_version_btn)
    Button checkVersion;

    public AboutDeveloperFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_developer, container, false);
        ButterKnife.bind(this, view);
        devVersion.setText("v" + getVersion());
        suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
        checkVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              checkVersion();
            }
        });
        return view;
    }

    //发邮件
    private void sendMail() {
        String emailSubject = "知乎日报ByDrizzle意见反馈";
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:drizzlezhang@gmail.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        startActivity(data);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本名
     */
    private String getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;
            int code = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    private int getVersionCode() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            int code = info.versionCode;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 检测更新，通过比较versioncode大小比较
     */
    private void checkVersion(){
        FIR.checkForUpdateInFIR(Config.FIRTOKEN, new VersionCheckCallback() {
            @Override
            public void onSuccess(final AppVersion appVersion, boolean b) {
                if (appVersion.getVersionCode() > getVersionCode()) {
                    String versionname = appVersion.getVersionName();
                    new MaterialDialog.Builder(getActivity())
                            .title("有新版本  v" + versionname).content("点击确定进行更新。")
                            .positiveText("确定").negativeText("取消")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    String updateUrl = appVersion.getUpdateUrl();
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)));
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                }
                            })
                            .show();
                } else {
                    TUtils.showShort(getActivity(), "当前为最新版本");
                }
            }

            @Override
            public void onFail(String s, int i) {
                Log.d("checkversion", "fail");
            }

            @Override
            public void onError(Exception e) {
                Log.d("checkversion", "error");
            }

            @Override
            public void onStart() {
                Log.d("checkversion", "start");
            }

            @Override
            public void onFinish() {
                Log.d("checkversion", "finish");
            }
        });
    }


}
