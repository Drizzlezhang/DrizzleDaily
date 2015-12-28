package com.drizzle.drizzledaily.api.model;

import java.util.List;

/**
 * Created by drizzle on 15/12/28.
 */
public class SectionList {


	private int timestamp;
	private String name;

	private List<StoriesEntity> stories;

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStories(List<StoriesEntity> stories) {
		this.stories = stories;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public String getName() {
		return name;
	}

	public List<StoriesEntity> getStories() {
		return stories;
	}

	public static class StoriesEntity {
		private String date;
		private String display_date;
		private int id;
		private String title;
		private List<String> images;

		public void setDate(String date) {
			this.date = date;
		}

		public void setDisplay_date(String display_date) {
			this.display_date = display_date;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setImages(List<String> images) {
			this.images = images;
		}

		public String getDate() {
			return date;
		}

		public String getDisplay_date() {
			return display_date;
		}

		public int getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public List<String> getImages() {
			return images;
		}
	}
}
