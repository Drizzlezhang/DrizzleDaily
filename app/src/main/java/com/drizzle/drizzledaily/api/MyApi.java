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
import rx.Observable;

/**
 * Created by drizzle on 15/12/28.
 */
public interface MyApi {

	@GET("start-image/{imgsize}")
	Call<StartImg> startImage( @Path("imgsize") String size);

	@GET("news/{pageid}")
	Observable<Story> story(@Path("pageid") int pageid);

	@GET("news/latest")
	Observable<LatestNews> latest();

	@GET("news/hot")
	Observable<HotNews> hot();

	@GET("news/before/{date}")
	Observable<BeforeNews> before(@Path("date") String date);

	@GET("themes")
	Observable<Themes> themes();

	@GET("theme/{themeid}")
	Observable<ThemeList> themelist(@Path("themeid") int themeid);

    @GET("sections")
	Observable<Sections> sections();

	@GET("section/{sectionid}")
	Observable<SectionList> sectionlist(@Path("sectionid") int sectionid);
}
