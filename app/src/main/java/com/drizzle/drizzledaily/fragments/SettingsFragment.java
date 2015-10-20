package com.drizzle.drizzledaily.fragments;

import android.content.Intent;
import android.os.Bundle;
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

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        strings = new String[]{"原装色", "火焰红", "冷酷蓝", "高级黑", "热烈橙", "生命绿", "高贵紫", "香蕉黄"};
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
                TUtils.showShort(getActivity(), "缓存已清理");
                break;
            default:
                break;
        }
        return true;
    }
}
