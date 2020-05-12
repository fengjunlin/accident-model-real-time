package com.fengjunlin.accident.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_PATTERN_ONE = "yyyyMMddHHmmss";

    /**
     * 获取系统的当前时间
     *
     * @return 返回结果数据
     */
    public static String getCurrentTime() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0:00");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 将传入的时间和和对应的时间格式进行解析，成为毫秒
     *
     * @param time       时间
     * @param timeformat
     * @return
     */
    public static Long getLongTime(String time, String timeformat) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeformat);
        Date startDate = null;
        try {

            startDate = sdf.parse(time);
        } catch (ParseException e) {
            return null;
        }
        // 获取毫秒数
        Long startLong = startDate.getTime();
        return startLong;
    }


    /**
     * 将utc时间转换为北京时间
     *
     * @throws ParseException
     */
    public static String UTCToCST(Long time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GST"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time + 8 * 1000 * 3600);
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将毫秒转换为具体的时间格式
     *
     * @param time   毫秒
     * @param format 时间格式
     * @return 返回转换后的结果
     */
    public static String longTimeToFormateTime(long time, String format) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }

    public static void main(String[] args) {

//        String yyyyMMddHHmmss = LongTimeToFormatTime(1567481202611, "yyyyMMddHHmmss");
//        System.out.println(yyyyMMddHHmmss);
    }
}
