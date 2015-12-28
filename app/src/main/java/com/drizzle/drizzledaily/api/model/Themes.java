package com.drizzle.drizzledaily.api.model;

import java.util.List;

/**
 * Created by drizzle on 15/12/28.
 */
public class Themes {


	private int limit;
	private List<?> subscribed;
	private List<OthersEntity> others;

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setSubscribed(List<?> subscribed) {
		this.subscribed = subscribed;
	}

	public void setOthers(List<OthersEntity> others) {
		this.others = others;
	}

	public int getLimit() {
		return limit;
	}

	public List<?> getSubscribed() {
		return subscribed;
	}

	public List<OthersEntity> getOthers() {
		return others;
	}

	public static class OthersEntity {
		private int color;
		private String thumbnail;
		private String description;
		private int id;
		private String name;

		public void setColor(int color) {
			this.color = color;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getColor() {
			return color;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public String getDescription() {
			return description;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
