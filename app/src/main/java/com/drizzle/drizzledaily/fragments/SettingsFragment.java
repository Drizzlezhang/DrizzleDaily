package com.drizzle.drizzledaily.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.ui.MainActivity;
import com.drizzle.drizzledaily.utils.TUtils;

/**
 * 设置fragment
 */
public class SettingsFragment extends PreferenceFragment {
    private String[] strings;

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
//            case "check":
//                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("check");
//                TUtils.showShort(getActivity(), checkBoxPreference.isChecked() + "");
//                break;
            case "theme":
                new MaterialDialog.Builder(getActivity())
                        .title("换个皮肤吧~")
                        .items(strings)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                SharedPreferences preferences = getActivity().getSharedPreferences(Config.SKIN_NUMBER, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt(Config.SKIN_NUMBER, which);
                                editor.commit();
                                preference.setTitle(strings[which]);
                            }
                        })
                        .positiveText(android.R.string.cancel)
                        .show();
                break;
            case "clearcache":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getActivity()).clearDiskCache();
                    }
                }).start();
                Glide.get(getActivity()).clearMemory();
                TUtils.showShort(getActivity(),"缓存已清理");
                break;
            default:
                break;
        }
        return true;
    }
}
