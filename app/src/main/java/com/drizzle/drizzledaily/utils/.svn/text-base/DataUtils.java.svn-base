package com.drizzle.drizzledaily.utils;

import java.util.Calendar;

/**
 * 处理日期工具类
 */
public class DataUtils {
    /**
     * 设置时间
     *
     * @param year
     * @param month
     * @param date
     * @return
     */
    public static Calendar setCalendar(int year, int month, int date) {
        Calendar cl = Calendar.getInstance();
        cl.set(year, month - 1, date);
        return cl;
    }

    /**
     * 获取当前时间的前一天时间
     *
     * @param cl
     * @return
     */
    public static Calendar getBeforeDay(Calendar cl) {
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day - 1);
        return cl;
    }

    /**
     * 获取当前时间的后一天时间
     *
     * @param cl
     * @return
     */
    public static Calendar getAfterDay(Calendar cl) {
        //使用roll方法进行回滚到后一天的时间
        //cl.roll(Calendar.DATE, 1);
        //使用set方法直接设置时间值
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day + 1);
        return cl;
    }

    /**
     * 打印时间
     *
     * @param cl
     */
    public static String printCalendar(Calendar cl) {
        String tday = "";
        String tmonth = "";
        String tyear = "";
        int year = cl.get(Calendar.YEAR);
        int month = cl.get(Calendar.MONTH) + 1;
        int day = cl.get(Calendar.DATE);
        if (day > 9) {
            tday = "" + day;
        } else {
            tday = 0 + "" + day;
        }
        if (month > 9) {
            tmonth = "" + month;
        } else {
            tmonth = 0 + "" + month;
        }
        tyear = "" + year;
        return tyear + tmonth + tday;
    }

    /**
     * 打印时间
     *
     * @param cl
     */
    public static String printDate(Calendar cl) {
        int year = cl.get(Calendar.YEAR);
        int month = cl.get(Calendar.MONTH) + 1;
        int day = cl.get(Calendar.DATE);
        return year + "年" + month + "月" + day + "日";
    }
}
