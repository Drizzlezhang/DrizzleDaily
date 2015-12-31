package com.drizzle.drizzledaily.api;

import com.drizzle.drizzledaily.api.model.BeforeNews;
import com.drizzle.drizzledaily.api.model.HotNews;
import com.drizzle.drizzledaily.api.model.LatestNews;
import com.drizzle.drizzledaily.api.model.SectionList;
import com.drizzle.drizzledaily.api.model.Sections;
import com.drizzle.drizzledaily.api.model.StartImg;
import com.drizzle.drizzledaily.api.model.Story;
import com.drizzle.drizzledaily.api.model.ThemeList;
import com.drizzle.drizzledaily.api.model.Themes;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by drizzle on 15/12/28.
 */
public interface MyApi {

	@GET("start-image/{imgsize}")
	Call<StartImg> startImage( @Path("imgsize") String size);

	@GET("news/{pageid}")
	Call<Story> story(@Path("pageid") int pageid);

	@GET("news/latest")
	Call<LatestNews> latest();

	@GET("news/hot")
	Call<HotNews> hot();

	@GET("news/before/{date}")
	Call<BeforeNews> before(@Path("date") String date);

	@GET("themes")
	Call<Themes> themes();

	@GET("theme/{themeid}")
	Call<ThemeList> themelist(@Path("themeid") int themeid);

    @GET("sections")
	Call<Sections> sections();

	@GET("section/{sectionid}")
	Call<SectionList> sectionlist(@Path("sectionid") int sectionid);
}
