package com.drizzle.drizzledaily.utils;

import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by drizzle on 16/3/31.
 */
public class CacheUtils {
	private static LruCache<String, String> mLruCache;

	private static CacheUtils mCacheUtils;

	private CacheUtils() {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = maxMemory / 8;
		mLruCache = new LruCache<String, String>(cacheSize) {
			@Override protected int sizeOf(String key, String value) {
				return value.length();
			}
		};
	}

	public static CacheUtils getInstance() {
		if (mCacheUtils == null) {
			mCacheUtils = new CacheUtils();
		}
		return mCacheUtils;
	}

	public void saveCache(String key, final String baseListItemList) {
		//if (mLruCache == null) {
		//	int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		//	int cacheSize = maxMemory / 8;
		//	mLruCache = new LruCache<String, String>(cacheSize) {
		//		@Override protected int sizeOf(String key, String value) {
		//			return value.length();
		//		}
		//	};
		//}
		mLruCache.put(key, baseListItemList);
		Log.d("save", baseListItemList.toString());
		Log.d("save", "success");
	}

	public String getCache(String key) {
		//if (mLruCache == null) {
		//	int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		//	int cacheSize = maxMemory / 8;
		//	mLruCache = new LruCache<String, String>(cacheSize) {
		//		@Override protected int sizeOf(String key, String value) {
		//			return value.length();
		//		}
		//	};
		//}
		Log.d("save", mLruCache.get(key) + "");
		return mLruCache.get(key);
	}

	public void removeCache(String key) {
		//if (mLruCache == null) {
		//	int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		//	int cacheSize = maxMemory / 8;
		//	mLruCache = new LruCache<String, String>(cacheSize) {
		//		@Override protected int sizeOf(String key, String value) {
		//			return value.length();
		//		}
		//	};
		//}
		mLruCache.remove(key);
	}
}