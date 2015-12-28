package com.drizzle.drizzledaily.api.model;

import java.util.List;

/**
 * Created by drizzle on 15/12/28.
 */
public class Sections {

	private List<DataEntity> data;

	public void setData(List<DataEntity> data) {
		this.data = data;
	}

	public List<DataEntity> getData() {
		return data;
	}

	public static class DataEntity {
		private String thumbnail;
		private String description;
		private String name;
		private int id;

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public String getDescription() {
			return description;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}
	}
}
