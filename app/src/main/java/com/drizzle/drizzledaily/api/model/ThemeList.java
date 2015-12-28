package com.drizzle.drizzledaily.api.model;

import java.util.List;

/**
 * Created by drizzle on 15/12/28.
 */
public class ThemeList {

	private String description;
	private String background;
	private int color;
	private String name;
	private String image;
	private String image_source;


	private List<StoriesEntity> stories;


	private List<EditorsEntity> editors;

	public void setDescription(String description) {
		this.description = description;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setImage_source(String image_source) {
		this.image_source = image_source;
	}

	public void setStories(List<StoriesEntity> stories) {
		this.stories = stories;
	}

	public void setEditors(List<EditorsEntity> editors) {
		this.editors = editors;
	}

	public String getDescription() {
		return description;
	}

	public String getBackground() {
		return background;
	}

	public int getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String getImage_source() {
		return image_source;
	}

	public List<StoriesEntity> getStories() {
		return stories;
	}

	public List<EditorsEntity> getEditors() {
		return editors;
	}

	public static class StoriesEntity {
		private int type;
		private int id;
		private String title;
		private List<String> images;

		public void setType(int type) {
			this.type = type;
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

		public int getType() {
			return type;
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

	public static class EditorsEntity {
		private String url;
		private String bio;
		private int id;
		private String avatar;
		private String name;

		public void setUrl(String url) {
			this.url = url;
		}

		public void setBio(String bio) {
			this.bio = bio;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public String getBio() {
			return bio;
		}

		public int getId() {
			return id;
		}

		public String getAvatar() {
			return avatar;
		}

		public String getName() {
			return name;
		}
	}
}
