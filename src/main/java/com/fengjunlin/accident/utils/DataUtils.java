package com.fengjunlin.accident.utils;

import com.alibaba.fastjson.JSONObject;
import com.fengjunlin.accident.model.BaseDataPointWithSpeed;
import com.fengjunlin.accident.model.Gps;
import com.fengjunlin.accident.tools.constants.DataConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 数据处理工具类
 * @Author fengjl
 * @Date 2019/4/24 11:19
 * @Version 1.0
 **/
public class DataUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * 根据速度来判断等级,后续要根据车的重量，停车的时间，天气，路面阻尼系数综合给出一个系数
     *
     * @param speed    速度
     * @param stopTime
     * @return 等级
     */
    public static Integer analysisLevel(double speed, double stopTime) {
        JSONObject jsonObject = ParamConfiguration.configration.getJSONObject(DataConstants.LEAVEL);
        // 轻微
        JSONObject jsonSligth = jsonObject.getJSONObject(DataConstants.SLIGHT);
        double minSlight = jsonSligth.getDouble(DataConstants.MIN);
        double maxSlight = jsonSligth.getDouble(DataConstants.MAX);
        // 中度
        JSONObject jsonModerate = jsonObject.getJSONObject(DataConstants.MODERATE);
        double minModerate = jsonModerate.getDouble(DataConstants.MIN);
        double maxModerate = jsonModerate.getDouble(DataConstants.MAX);
        // 严重
        JSONObject jsonSeverity = jsonObject.getJSONObject(DataConstants.SEVERITY);
        double minSeverity = jsonSeverity.getDouble(DataConstants.MIN);
        double maxSeverity = jsonSeverity.getDouble(DataConstants.MAX);

        if (minSlight <= speed && speed < maxSlight) {
            return 1;
        } else if (minModerate <= speed && speed < maxModerate) {
            return 2;
        } else {
            return 3;
        }
    }

    public static void findNeighPosition(double longitude, double latitude) {
        //先计算查询点的经纬度范围
        double r = 6378.138;//地球半径千米
        double dis = 0.05;//0.5千米距离
        double dlng = 2 * Math.asin(Math.sin(dis / (2 * r)) / Math.cos(latitude * Math.PI / 180));
        dlng = dlng * 180 / Math.PI;//角度转为弧度
        double dlat = dis / r;
        dlat = dlat * 180 / Math.PI;
        double minlat = latitude - dlat;
        double maxlat = latitude + dlat;
        double minlng = longitude - dlng;
        double maxlng = longitude + dlng;
        String aa = "SELECT * FROM traffic_lights WHERE traffic_lights.lat > " + minlat + " AND traffic_lights.lat < " + maxlat + "AND traffic_lights.lng > " + minlng + " AND traffic_lights.lng < " + maxlng;
        System.out.println(aa);

    }


    /**
     * 主要用于解析成为的数据
     *
     * @param lng  经度
     * @param lat  维度
     * @param time 时间
     * @return 模型数据点
     */
    public static BaseDataPointWithSpeed paseData(String lng, String lat, String time, double speed, long currentTime) {
        try {
            // 坐标转换成为高德坐标
            Gps gps = PositionUtil.gps84_To_Gcj02(lat, lng);
            Long longTime = DateUtils.getLongTime(time, DateUtils.DATE_TIME_PATTERN);
            BaseDataPointWithSpeed baseDataPoint = new BaseDataPointWithSpeed(gps.getWgLon(), gps.getWgLat(), longTime, speed, currentTime);
            return baseDataPoint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        findNeighPosition(116.359323,39.967894);
    }

}
