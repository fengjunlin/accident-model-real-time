package com.fengjunlin.accident.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengjunlin.accident.model.*;
import com.fengjunlin.accident.tools.BaseDataAndAccidentCache;
import com.fengjunlin.accident.tools.constants.DataConstants;
import com.fengjunlin.accident.tools.constants.TripKeyConstants;
import com.fengjunlin.accident.utils.DataUtils;
import com.fengjunlin.accident.utils.DateUtils;
import com.fengjunlin.accident.utils.ParamConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description
 * @Author fengjl
 * @Date 2019/8/22 17:07
 * @Version 1.0
 **/
@Component
public class ZipDataDataHander {
    private static final Logger logger = LoggerFactory.getLogger(ZipDataDataHander.class);

    public void zipDataHander(String deviceId, JSONArray zipData) {
        List<BaseDataPointWithSpeed> list = new ArrayList<>(16);
        Long currentTime = System.currentTimeMillis();
        for (int i = 0; i < zipData.size(); i++) {
            try {
                JSONObject jsonObject = zipData.getJSONObject(i);
                String time = jsonObject.getString(TripKeyConstants.TIME.getKey());
                Double lng = jsonObject.getDouble(TripKeyConstants.LNG.getKey());
                Double lat = jsonObject.getDouble(TripKeyConstants.LAT.getKey());
                Double speed = jsonObject.getDouble(TripKeyConstants.SPEED.getKey());
                if (lng > 20 && lat > 20) {
                    BaseDataPointWithSpeed baseDataPoint = DataUtils.paseData(String.valueOf(lng), String.valueOf(lat), time, speed, currentTime);
                    list.add(baseDataPoint);
                }
            } catch (Exception e) {
                continue;
            }
        }
        Collections.sort(list);
        dealPreDataBlock(deviceId, list);
    }

    private List<ModelDataPointWithSpeed> loadModelDataPoint(List<BaseDataPointWithSpeed> dataPoints) {
        Double accelerationMark = ParamConfiguration.configration.getDouble(DataConstants.ACCELERATION_MARK);
        List<ModelDataPointWithSpeed> modelDataPoints = new ArrayList<>(16);
        List<ModelDataPointWithSpeed> probableAccidentPoint = new ArrayList<>(16);
        for (int i = 0; i < dataPoints.size() - 1; i++) {
            ModelDataPointWithSpeed modeDataPoint = dataPoints.get(i).getModelDataPointWithSpeed(dataPoints.get(i + 1));
            // 排除算出来的异常数据
            if (modeDataPoint.getAcceleratedSpeed() < 100 && modeDataPoint.getAcceleratedSpeed() > -100 && modeDataPoint.getLongTime() != 0) {
                modelDataPoints.add(modeDataPoint);
            }
        }
        Collections.sort(modelDataPoints);
        for (ModelDataPointWithSpeed modelDataPoint : modelDataPoints) {
            double acceleratedSpeed = modelDataPoint.getAcceleratedSpeed();
            if (acceleratedSpeed < accelerationMark) {
                probableAccidentPoint.add(modelDataPoint);
            }
            Long time = modelDataPoint.getBaseDataPointWithSpeed().getTime();
            String times = DateUtils.UTCToCST(time);
            double speed = modelDataPoint.getPreSpeed();
            double acceleratedSpeeds = (double) Math.round(modelDataPoint.getAcceleratedSpeed() * 100) / 100;
            logger.info("speed： " + speed + "km/h     AcceleratedSpeed：" + acceleratedSpeeds + "     time:" + times);
            if (modelDataPoint.getAcceleratedSpeed() <= -1.5) {
                logger.info("可能的事故点： " + "[ " + ("speed： " + speed + "km/h     AcceleratedSpeed：" + acceleratedSpeed + "     time:" + times) + "  lng:" + modelDataPoint.getBaseDataPointWithSpeed().getLng() + "  lat: " + modelDataPoint.getBaseDataPointWithSpeed().getLat() + " ]");
            }
        }
        return probableAccidentPoint;
    }

    /**
     * 当前处理的30s数据和上一次传递的30s的数据块做衔接
     *
     * @param list
     * @return
     */
    private void dealPreDataBlock(String deviceId, List<BaseDataPointWithSpeed> list) {
        try {
            List<BaseDataPointWithSpeed> startList = list;
            Map<String, List<BaseDataPointWithSpeed>> map = BaseDataAndAccidentCache.map;
            List<BaseDataPointWithSpeed> preDataBlocks = map.get(deviceId);
            if (preDataBlocks == null) {
                map.put(deviceId, list);
            } else {
                BaseDataPointWithSpeed baseDataPointWithSpeed = preDataBlocks.get(preDataBlocks.size() - 1);
                BaseDataPointWithSpeed firstBaseData = list.get(0);
                double timeBetweenTwoPoints = baseDataPointWithSpeed.getTimeBetweenTwoPoints(firstBaseData);
                // 前一个最后一个点和本次最开始的点不超过5s连接在一起
                if (timeBetweenTwoPoints < 5) {
                    list.add(0, baseDataPointWithSpeed);
                }
                // 超过五分钟直接更新
                else if (timeBetweenTwoPoints > 60) {
                    map.put(deviceId, list);
                }
            }

            List<ModelDataPointWithSpeed> modelDataPointWithSpeeds = loadModelDataPoint(list);
            if (modelDataPointWithSpeeds.size() != 0) {
                ModelDataPointWithSpeed modelAcceleratedSpeed = modelDataPointWithSpeeds.get(0);
                for (ModelDataPointWithSpeed m : modelDataPointWithSpeeds) {
                    if (m.getAcceleratedSpeed() < modelAcceleratedSpeed.getAcceleratedSpeed()) {
                        modelAcceleratedSpeed = m;
                    }
                }
                List<BaseDataPointWithSpeed> preData = map.get(deviceId);
                Long systemTime = preData.get(0).getSystemTime();
                if (systemTime != modelAcceleratedSpeed.getBaseDataPointWithSpeed().getSystemTime()) {
                    preData.addAll(startList);
                    AccidentModel maxSpeedByTenSecond = findMaxSpeedByTenSecond(preData, modelAcceleratedSpeed, deviceId);
                    BaseDataAndAccidentCache.accidentList.add(maxSpeedByTenSecond);
                    map.put(deviceId, list);
                }
                // 如果没有前面的情况单独处理
                else {
                    AccidentModel maxSpeedByTenSecond = findMaxSpeedByTenSecond(list, modelAcceleratedSpeed, deviceId);
                    BaseDataAndAccidentCache.accidentList.add(maxSpeedByTenSecond);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 分析发生碰撞前的一个情况
     *
     * @return
     */
    private AccidentModel findMaxSpeedByTenSecond(List<BaseDataPointWithSpeed> list, ModelDataPointWithSpeed model, String deviceId) {
        BaseDataPointWithSpeed baseDataPointWithSpeed = model.getBaseDataPointWithSpeed();
        int i = list.indexOf(baseDataPointWithSpeed);
        List<BaseDataPointWithSpeed> preList = new ArrayList<>();
        if (i < 10) {
            for (int w = 0; w <= i; w++) {
                preList.add(list.get(w));
            }
        } else {
            for (int w = (i - 10); w <= i; w++) {
                preList.add(list.get(w));
            }
        }

        List<Double> speeds = new ArrayList<>();
        List<Gps> track = new ArrayList<>();
        for (BaseDataPointWithSpeed d : preList) {
            speeds.add(d.getSpeed());
            Gps gps = new Gps(d.getLat(), d.getLng());
            track.add(gps);
        }
        DoubleSummaryStatistics speedsStats = speeds.stream().mapToDouble(x -> x).summaryStatistics();
        double avrageSpeed = speedsStats.getAverage();
        double maxSpeed = speedsStats.getMax();
        double minSpeed = speedsStats.getMin();
        AccidentModel accidentModel = new AccidentModel(deviceId, avrageSpeed, maxSpeed, minSpeed, model, track);
        return accidentModel;
    }

}