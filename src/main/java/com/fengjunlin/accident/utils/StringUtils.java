package com.fengjunlin.accident.utils;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @Description 字符串处理类
 */
public interface StringUtils {

    Pattern NUMBER_OR_LETTER = compile("^[a-z0-9A-Z]+$");

    Pattern LNG_LAT_PATTERN = Pattern.compile("^((-?(([1-9][0-9])|(1[0-7][0-9]))([.])?\\d*)|(180))[,]((-?(([1-8][0-9])|)([.])?\\d*)|(90))$");

    /**
     * 判断字符串是否为空
     *
     * @param data
     * @return
     */
    static boolean isEmpty(String data) {
        return (null == data || data.trim().length() == 0 || "null".equals(data));
    }

    /**
     * 判断数据是否是经纬度
     *
     * @param lng 经度
     * @param lat 纬度
     * @return
     */
    static boolean isLngAndLat(String lng, String lat) {
        if (isEmpty(lng) || isEmpty(lat)) {
            return false;
        }
        return LNG_LAT_PATTERN.matcher(lng + "," + lat).matches();
    }

    /**
     * 判断是否为数字或字母
     *
     * @param str
     * @return
     */
    static boolean isNumberOrLetter(String str) {
        return isEmpty(str) ? false : NUMBER_OR_LETTER.matcher(str).matches();
    }

}
