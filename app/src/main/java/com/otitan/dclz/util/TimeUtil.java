package com.otitan.dclz.util;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sp on 2018/10/16.
 * 时间转换工具
 */

public class TimeUtil {
    private static final String WEBURL_BAIDU = "http://www.beijing-time.org";
    private static TimeUtil instence = null;

    public synchronized static TimeUtil getInstence() {
        if (instence == null) {
            instence = new TimeUtil();
        }
        return instence;
    }

    /**
     * 获取网络上的时间
     */
    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        return getConnect(sdf);
    }

    /**
     * 获取中央时区时间，北京时间是在东八区，所以北京时间= 格林时间+8小时
     */
    public static String getGMT() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        String str = sdf.format(cd.getTime());
        return str;
    }

    /**
     * 将毫秒数转为小时数据
     */
    public static double getMisToHours(long mis) {
        return (mis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
    }

    /**
     * 将毫秒数转为天数
     */
    public static double getMisToDays(long mis) {
        return (mis / (1000 * 60 * 60 * 24));
    }

    //毫秒转秒
    public static String long2String(long time) {

        //毫秒转秒
        int sec = (int) time / 1000;
        int min = sec / 60; // 分钟
        sec = sec % 60; // 秒
        if (min < 10) { // 分钟补0
            if (sec < 10) { // 秒补0
                return "0" + min + ":0" + sec;
            } else {
                return "0" + min + ":" + sec;
            }
        } else {
            if (sec < 10) { // 秒补0
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        }

    }

    /**
     * 返回当前时间
     * 时间格式: yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return getConnect(sdf);
    }

    /**
     * 将时间转化成毫秒
     * 时间格式: yyyy-MM-dd HH:mm:ss
     */
    public static long getLonfromStr(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeStrToSecond(time, format);
    }

    /**
     * 将时间转化成毫秒
     * 时间格式: yyyy-MM-dd HH:mm
     */
    public static long getLonfromNyrsf(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return timeStrToSecond(time, format);
    }

    /**
     * 将时间转化成毫秒
     * 时间格式: yyyy-MM-dd
     */
    public static long getLonfromYyr(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return timeStrToSecond(time, format);
    }

    /**
     * 将时间转化成毫秒
     * 时间格式: HH:mm:ss
     */
    public static long getLonfromSfm(String time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return timeStrToSecond(time, format);
    }

    /**
     * 将不同格式时间转化成毫秒
     * 时间格式: SimpleDateFormat sdf
     */
    private static Long timeStrToSecond(String time, SimpleDateFormat sdf) {
        if (time.equals("")) {
            return -0l;
        }
        try {
            Long second = sdf.parse(time).getTime();
            return second;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -0l;
    }

    private static String getConnect(final SimpleDateFormat sdf) {
        try {
            URL url = new URL(WEBURL_BAIDU); // 取得资源对象
            URLConnection uc = url.openConnection(); // 生成连接对象
            uc.connect(); // 发出连接
            long ld = uc.getDate(); // 取得网站日期时间
            Date date = new Date(ld); // 转换为标准时间对象
            // SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
            String nowtime = sdf.format(date);
            return nowtime;
        } catch (Exception e) {
            return sdf.format(new Date());
        }
    }
}
