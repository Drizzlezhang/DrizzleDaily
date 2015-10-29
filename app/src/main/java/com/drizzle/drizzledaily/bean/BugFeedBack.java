package com.drizzle.drizzledaily.bean;

import cn.bmob.v3.BmobObject;

/**
 * 反馈数据表
 */
public class BugFeedBack extends BmobObject {
    private String feedBackContents;
    private String model;

    public String getFeedBackContents() {
        return feedBackContents;
    }

    public void setFeedBackContents(String feedBackContents) {
        this.feedBackContents = feedBackContents;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
