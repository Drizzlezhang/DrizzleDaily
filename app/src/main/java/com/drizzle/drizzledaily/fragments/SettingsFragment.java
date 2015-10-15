package com.drizzle.drizzledaily.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.utils.TUtils;

/**
 * 设置fragment
 */
public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "check":
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("check");
                TUtils.showShort(getActivity(), checkBoxPreference.isChecked() + "");
                break;
            default:
                break;
        }
        return true;
    }
}
