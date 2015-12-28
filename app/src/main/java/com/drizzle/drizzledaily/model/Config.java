package com.drizzle.drizzledaily.model;

import android.os.Environment;

import java.io.File;

/**
 * 数据请求地址
 */
public class Config {
    

    /**
     * 存放之前的数据库到sharedperence的标识
     */
    public static final String COLLECTCACHE = "collectcache";
    public static final String COLLECTVERSION = "collectversion";
    /**
     * 存放的文章,图片唯一id
     */
    public static final String READID = "readid";
    public static final String IMAGEURL = "imageurl";
    /**
     * 申请到的微信id,fir token,bugtestid
     */
    public static final String WXAPPID = "wx370c315141dbd99b";
    public static final String FIRTOKEN = "0907463c4425f95f233aa8ed32d7880f";
    public static final String BUGTESTID = "73e619a25cafd56c1119b8800ddc7697";
    /**
     * 存放图片地址
     */
    public static final String PHOTO_FOLDER = new File(Environment.getExternalStorageDirectory(), "").getPath() + "/drizzledaily/pics";

    public static final String START_PHOTO_FOLDER = new File(Environment.getExternalStorageDirectory(), "").getPath() + "/drizzledaily/start";
    /**
     * 存放皮肤代号的sharedperference文件地址
     */
    public static final String SKIN_NUMBER = "skinnumber";
    /**
     * 存放缓存数据,首页图片地址的sharedperference文件地址
     */
    public static final String CACHE_DATA = "cachedata";
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

    public static final String THEME_LIST_EVERY = "http://news-at.zhihu.com/api/4/theme/";


    /**
     * 知乎日报栏目列表
     * http://news-at.zhihu.com/api/3/sections
     * 拼接对应id获取对应栏目列表 http://news-at.zhihu.com/api/3/sections/1
     */
    public static final String SECTION_LIST = "http://news-at.zhihu.com/api/3/sections";

    public static final String SECTION_LIST_EVERY = "http://news-at.zhihu.com/api/3/section/";

    /**
     * 热门消息列表
     * http://news-at.zhihu.com/api/3/news/hot
     */
    public static final String Hot_NEWS = "http://news-at.zhihu.com/api/3/news/hot";

    /**
     * 头像地址
     */
    public static final String TOUXIANGURL = "http://d.hiphotos.baidu.com/baike/w%3D268/sign=f456853874cf3bc7e800caeae900babd/962bd40735fae6cde8d6d2540bb30f2442a70fa0.jpg";
}
