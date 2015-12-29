package com.drizzle.drizzledaily.bean;


/**
 * 列表项bean，包括文章id，标题，图片地址,是否已经被点击,文章日期，描述
 */
public class BaseListItem{



    private int id;
    private int viewType = 1;
    private String title;
    private String imgUrl;
    private boolean isClicked;
    private String date;
    private String describe;

    public BaseListItem() {
    }

    public BaseListItem(int id, String title, String imgUrl, boolean isClicked, String date) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
        this.isClicked = isClicked;
        this.date = date;
    }

    public BaseListItem(int id, String title, String imgUrl, boolean isClicked, String date, String describe) {
        this.id = id;
        this.describe = describe;
        this.title = title;
        this.imgUrl = imgUrl;
        this.isClicked = isClicked;
        this.date = date;
    }

    public BaseListItem(int id, int viewType, String title, String imgUrl, boolean isClicked, String date, String describe) {
        this.id = id;
        this.viewType = viewType;
        this.title = title;
        this.imgUrl = imgUrl;
        this.isClicked = isClicked;
        this.date = date;
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
