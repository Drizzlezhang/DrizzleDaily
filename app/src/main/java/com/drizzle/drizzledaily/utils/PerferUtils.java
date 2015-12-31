package com.drizzle.drizzledaily.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.drizzle.drizzledaily.model.Config;
import com.drizzle.drizzledaily.model.MyApplication;

/**
 * Created by drizzle on 15/12/23.
 */
public class PerferUtils {

	private static SharedPreferences getSharedPerference(String saveName) {
		return MyApplication.getContext().getSharedPreferences(saveName, Context.MODE_PRIVATE);
	}

	public static void saveSth(String key, String sth) {
		SharedPreferences.Editor editor = getSharedPerference(Config.CACHE_DATA).edit();
		editor.putString(key, sth);
		editor.apply();
	}

	public static void saveSth(String key, int sth) {
		SharedPreferences.Editor editor = getSharedPerference(Config.CACHE_DATA).edit();
		editor.putInt(key, sth);
		editor.apply();
	}

	public static void deleteSth(String key) {
		SharedPreferences.Editor editor = getSharedPerference(Config.CACHE_DATA).edit();
		editor.remove(key);
		editor.apply();
	}

	public static String getString(String key) {
		String s = getSharedPerference(Config.CACHE_DATA).getString(key, "");
		return s;
	}

	public static int getInt(String key) {
		int i = getSharedPerference(Config.CACHE_DATA).getInt(key, 0);
		return i;
	}
}
