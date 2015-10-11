package com.drizzle.drizzledaily.bean;

/**
 * 列表项bean，包括文章id，标题，图片地址,是否已经被点击
 */
public class BaseListItem {
    private int id;
    private String title;
    private String imgUrl;
    private boolean isClicked;
    private boolean isPics;

    public BaseListItem() {
    }

    public BaseListItem(int id, String title, String imgUrl, boolean isClicked,boolean isPics) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
        this.isClicked = isClicked;
        this.isPics=isPics;
    }

    public boolean isPics() {
        return isPics;
    }

    public void setIsPics(boolean isPics) {
        this.isPics = isPics;
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

    public boolean isClicked() {
        return isClicked;
    }

    public void setIsClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }
}
