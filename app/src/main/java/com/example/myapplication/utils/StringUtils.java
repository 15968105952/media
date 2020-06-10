package com.example.myapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangdh on 2016/11/14.
 */
public class StringUtils {
    /**
     * 格式化时间: ms -- mm:ss
     * @param ms
     * @return
     */
    public static String formatTime(long ms) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(ms));
    }

    /**
     * 格式化 当前系统时间  ms -- HH:mm:ss 24H  hh:12h
     * @return
     */
    public static String formatSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());//默认当前系统时间
    }
}
