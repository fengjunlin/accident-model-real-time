package com.fengjunlin.accident.tools.constants;

import com.fengjunlin.accident.model.BaseDataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author fengjl
 * @Date 2019/6/28 20:17
 * @Version 1.0
 **/
public class KafKaConstant {

    public static List<BaseDataPoint> lists;

    static {
        lists = new ArrayList<>();
    }

    /**
     * kafka分区个数
     */
    public static int PARTITION_COUNT = 3;

    /**
     * 成为行程的数据kafka中的key
     */
    public static final String TRIP = "TRIP";

    /**
     * 成为GPS数据
     */
    public static final String TRAIL = "TRAIL";

    /**
     * 行程统计的kafka的Topic,暂且设置为3个分区
     */
    public static final String TRAVEL_DATA = "CW_DATA_TRAVE";

    /**
     * 发往kafka的key,通过这个来区分不同的设备
     */
    public static final String KEY = "device_chengwei_info";

    /**
     * 设备是否点火的标志
     */

    /**
     * 车辆是否定位的标志
     */
    public static final String POSI ="positionStatus";
}
