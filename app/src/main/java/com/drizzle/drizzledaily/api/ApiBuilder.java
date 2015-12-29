package com.drizzle.drizzledaily.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import java.util.List;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by drizzle on 15/12/28.
 * 网络请求基类
 */

public class ApiBuilder {

	public static final OkHttpClient client = new OkHttpClient();

	private static final String BASEURL = "http://news-at.zhihu.com/api/4/";

	private static final Gson gson =
		new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	private static final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASEURL)
		.client(client)
		.addConverterFactory(GsonConverterFactory.create(gson))
		.build();

	public static <T> T create(Class<T> clazz) {
		return retrofit.create(clazz);
	}
}
