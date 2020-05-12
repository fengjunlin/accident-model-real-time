package com.fengjunlin.accident.tools.constants;

/**
 * Hbase 查询相关常量
 */
public class HbaseQueryConstants {
    /**
     * 成为OBD 表名
     */
    public static final String CHENGWEI_TABLE_NAME = "device_chengwei_info";
    /**
     * 成为GPS 定位数据列簇
     */
    public static final String CHENGWEI_GPS_FAMILY_NAME = "trail";
    /**
     * GPS rowkey前缀
     */
    public static final String CHENGWEI_ROW_KEY_PREFIX = "cw_";
}

