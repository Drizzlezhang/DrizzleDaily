package com.drizzle.drizzledaily.bean;

/**
 * 分享列表项
 */
public class ShareBean {
	private int imgId;
	private String text;

	public ShareBean() {
	}

	public ShareBean(int imgId, String text) {
		this.imgId = imgId;
		this.text = text;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
