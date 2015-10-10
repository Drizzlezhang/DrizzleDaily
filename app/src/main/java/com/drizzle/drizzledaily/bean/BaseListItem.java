package com.drizzle.drizzledaily.bean;

/**
 * 列表项bean，包括文章id，标题，图片地址
 */
public class BaseListItem {
    private int id;
    private String title;
    private String imgUrl;

    public BaseListItem() {
    }

    public BaseListItem(int id, String title, String imgUrl) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
