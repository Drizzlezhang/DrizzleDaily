package com.drizzle.drizzledaily.fragments;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.drizzle.drizzledaily.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 关于开发者页面，意见反馈
 */
public class AboutDeveloperFragment extends android.support.v4.app.Fragment {
    @Bind(R.id.about_dev_versionname)
    TextView devVersion;

    @Bind(R.id.suggestion_btn)
    Button suggestionBtn;

    public AboutDeveloperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_developer, container, false);
        ButterKnife.bind(this, view);
        devVersion.setText("v"+getVersion());
        suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
        return view;
    }

    //发邮件
    private void sendMail() {
        String emailSubject = "知乎日报ByDrizzle意见反馈";
        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:drizzlezhang@gmail.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        startActivity(data);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
