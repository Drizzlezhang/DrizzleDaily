package com.drizzle.drizzledaily.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.PhotoActivity;
import com.drizzle.drizzledaily.utils.TUtils;

/**
 * 设置fragment
 */
public class SettingsFragment extends PreferenceFragment {
    private String[] strings;
    public static final String STARTIMGCACHEURL = Config.START_PHOTO_FOLDER + "/startimg.jpg";
    private ProgressDialog progressDialog;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        strings = new String[]{"原装色", "火焰红", "冷酷蓝", "高级黑", "热烈橙", "生命绿", "高贵紫", "香蕉黄"};
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("请稍等...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {
        switch (preference.getKey()) {
            case "seestartimg":
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra(Config.IMAGEURL, STARTIMGCACHEURL);
                startActivity(intent);
                break;
//            case "theme":
//                new MaterialDialog.Builder(getActivity())
//                        .title("换个皮肤吧~")
//                        .items(strings)
//                        .itemsCallback(new MaterialDialog.ListCallback() {
//                            @Override
//                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                SharedPreferences preferences = getActivity().getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.putInt(Config.SKIN_NUMBER, which);
//                                editor.commit();
//                                preference.setTitle(strings[which]);
//                            }
//                        })
//                        .positiveText(android.R.string.cancel)
//                        .show();
//                break;
            case "clearcache":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
        @Override
        public void handleMessage(Message msg) {
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
