package com.drizzle.drizzledaily.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 */
public class TUtils {
	private static Toast toast;

	private TUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 短时间显示Toast
	 */
	public static void showShort(Context context, CharSequence message) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 短时间显示Toast
	 */
	public static void showShort(Context context, int message) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 */
	public static void showLong(Context context, CharSequence message) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 */
	public static void showLong(Context context, int message) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		} else {
			toast.setText(message);
		}
		toast.show();
	}
}
