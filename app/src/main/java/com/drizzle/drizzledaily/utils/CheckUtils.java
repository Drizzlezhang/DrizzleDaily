package com.drizzle.drizzledaily.utils;

import com.drizzle.drizzledaily.model.Config;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Set;

/**
 * Created by drizzle on 16/3/18.
 */
public class CheckUtils {

	public static boolean checkIsAlreadyClick(int id) {
		Gson gson = new Gson();
		String alreadyclick = PerferUtils.getStringList(Config.ALREADY_CLICK);
		Set<Integer> alreadySet = gson.fromJson(alreadyclick, new TypeToken<Set<Integer>>() {
		}.getType());
		if (alreadySet.contains(id)) {
			return true;
		} else {
			return false;
		}
	}
}
