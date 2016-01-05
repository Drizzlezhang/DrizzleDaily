package com.drizzle.drizzledaily.api.model;

import java.util.List;

/**
 * Created by drizzle on 15/12/28.
 */
public class HotNews {

	private List<RecentEntity> recent;

	public void setRecent(List<RecentEntity> recent) {
		this.recent = recent;
	}

	public List<RecentEntity> getRecent() {
		return recent;
	}

	public static class RecentEntity {
		private int news_id;
		private String url;
		private String thumbnail;
		private String title;

		public void setNews_id(int news_id) {
			this.news_id = news_id;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getNews_id() {
			return news_id;
		}

		public String getUrl() {
			return url;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public String getTitle() {
			return title;
		}
	}
}
