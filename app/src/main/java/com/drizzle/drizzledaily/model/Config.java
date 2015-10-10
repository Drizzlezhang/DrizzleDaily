package com.drizzle.drizzledaily.model;

import android.os.Environment;

import java.io.File;

/**
 * 数据请求地址
 */
public class Config {
    /**
     * 存放图片地址
     */
    public static final String PHOTO_FOLDER = new File(Environment.getExternalStorageDirectory(), "").getPath() + "/drizzledaily/pics";
    public static final String START_PHOTO_FOLDER = new File(Environment.getExternalStorageDirectory(), "").getPath() + "/drizzledaily/start";

    /**
     * 引导页图片,四个分辨率
     * http://news-at.zhihu.com/api/4/start-image/（1080*1776/320*432/480*728/720*1184)
     */
    public static final String START_IMAGE = "http://news-at.zhihu.com/api/4/start-image/";

    /**
     * 最新消息，包括顶部轮转图片及内容以及当天的列表
     * http://news-at.zhihu.com/api/4/news/latest
     */
    public static final String LATEST_NEWS = "http://news-at.zhihu.com/api/4/news/latest";

    /**
     * 消息详情，根据列表中提供的id拼接获取
     * http://news-at.zhihu.com/api/4/news/3892357
     */
    public static final String NEWS_BODY = "http://news-at.zhihu.com/api/4/news/";

    /**
     * 过往消息列表，根据日期获取
     * http://news.at.zhihu.com/api/4/news/before/20131119
     */
    public static final String BEFORE_NEWS = "http://news.at.zhihu.com/api/4/news/before/";

    /**
     * 主题日报列表
     * http://news-at.zhihu.com/api/4/themes
     * 拼接对应id获取改主题列表 http://news-at.zhihu.com/api/4/themes/2
     */
    public static final String THEME_LIST = "http://news-at.zhihu.com/api/4/themes";

    /**
     * 知乎日报栏目列表
     * http://news-at.zhihu.com/api/3/sections
     * 拼接对应id获取对应栏目列表 http://news-at.zhihu.com/api/3/sections/1
     */
    public static final String SECTION_LIST = "http://news-at.zhihu.com/api/3/sections";

    /**
     * 热门消息列表
     * http://news-at.zhihu.com/api/3/news/hot
     */
    public static final String Hot_NEWS = "http://news-at.zhihu.com/api/3/news/hot";
}
