package com.fengjunlin.accident.service;

import com.fengjunlin.accident.dao.hbase.HbaseDao;
import com.fengjunlin.accident.model.AccidentModel;
import com.fengjunlin.accident.model.BaseDataPointWithSpeed;
import com.fengjunlin.accident.model.HbaseSearchData;
import com.fengjunlin.accident.tools.BaseDataAndAccidentCache;
import com.fengjunlin.accident.tools.KafkaProducerTool;
import com.fengjunlin.accident.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 处理事故点的服务层
 * @Author fengjl
 * @Date 2019/9/2 14:51
 * @Version 1.0
 **/
@SuppressWarnings("all")
@Component
public class AccidentPointDeal {
    private static final Logger logger = LoggerFactory.getLogger(AccidentPointDeal.class);

    @Autowired
    HbaseDao hbaseDao;

    @Autowired
    KafkaProducerTool kafkaProducerTool;

    /**
     * 主要用于排除事故点
     */
    public void excludeAccidentPoint() {
        // 数据上游的事故点
        List<AccidentModel> accidentList = BaseDataAndAccidentCache.accidentList;
        // 装要移除的数据
        List<AccidentModel> remoreList = new ArrayList<>();
        for (int i = 0; i < accidentList.size(); i++) {
            AccidentModel accidentModel = accidentList.get(i);
            HbaseSearchData startAndEndRowKey = getStartAndEndRowKey(accidentModel);
            List<BaseDataPointWithSpeed> dataPoint = hbaseDao.getDataFromChengWeiOBDTableWithSpeed(startAndEndRowKey);
            Integer mark = accidentModel.getMark();

            // 排除事故数据点和清理
            if (dataPoint.size() != 0) {
                if (mark == 0) {
                    // 从碰撞的那个开始时间开始查
                    Long time = accidentModel.getModelDataPointWithSpeed().getBaseDataPointWithSpeed().getTime();
                    // 从识别出来的碰撞点10s之后的数据开始排除,也可以是5s钟
                    boolean b = dealPoint(dataPoint, time + 10 * 1000);
                    // 排除已经判断是碰撞点，之后车子开始有速度了，这种情况
                    if (!b) {
                        remoreList.add(accidentModel);
                        logger.info("移除的碰撞点位置：" + accidentModel);
                    }
                    BaseDataPointWithSpeed baseDataPointWithSpeed = dataPoint.get(dataPoint.size() - 1);
                    accidentModel.setMarkTimeTwo(baseDataPointWithSpeed.getTime());
                } else {
                    // 从上次结束的位置开始查询
                    Long markTimeTwo = accidentModel.getMarkTimeTwo();
                    // 排除已经判断是碰撞点，之后车子开始有速度了，这种情况
                    boolean b;
                    if (markTimeTwo != null) {
                        b = dealPoint(dataPoint, markTimeTwo);
                    } else {
                        b = dealPoint(dataPoint, accidentModel.getModelDataPointWithSpeed().getBaseDataPointWithSpeed().getTime());
                    }
                    if (!b) {
                        remoreList.add(accidentModel);
                        logger.info("移除的碰撞点位置：" + accidentModel);
                    }
                    BaseDataPointWithSpeed baseDataPointWithSpeed = dataPoint.get(dataPoint.size() - 1);
                    accidentModel.setMarkTimeTwo(baseDataPointWithSpeed.getTime());
                }
            }

            // 发送事故数据点和清理事故数据点
            if (mark >= 3) {
                // 此处为真正的事故点
                remoreList.add(accidentModel);
                logger.info("真正的碰撞点：" + accidentModel);
            }
        }
        for (int i = 0; i < remoreList.size(); i++) {
            accidentList.remove(remoreList.get(i));
        }
    }

    /**
     * 封装Hbase查询参数
     *
     * @return HbaseSearch
     */
    public static HbaseSearchData getStartAndEndRowKey(AccidentModel accidentModel) {
        // 事故点的时间
        Long time = accidentModel.getModelDataPointWithSpeed().getBaseDataPointWithSpeed().getTime();
        // 设备号
        String deviceId = accidentModel.getDeviceId();
        Integer mark = accidentModel.getMark();
        if (mark == null) {
            // 主要用于成为的数据格式的特性
            String startFormateTime = DateUtils.longTimeToFormateTime(time - 30 * 1000, DateUtils.DATE_TIME_PATTERN_ONE);
            String endFormateTime = DateUtils.longTimeToFormateTime(time + 60 * 1000, DateUtils.DATE_TIME_PATTERN_ONE);
            HbaseSearchData hbaseSearchData = new HbaseSearchData(deviceId, startFormateTime, endFormateTime);
            accidentModel.setMarkTime(time + 60 * 1000);
            accidentModel.setMark(0);
            accidentModel.setSystemTimeMarkTime(System.currentTimeMillis());
            return hbaseSearchData;
        } else {
            Long startTime = accidentModel.getMarkTime();
            Long endTime = startTime + 60 * 1000;
            String startFormateTime = DateUtils.longTimeToFormateTime(startTime, DateUtils.DATE_TIME_PATTERN_ONE);
            String endFormateTime = DateUtils.longTimeToFormateTime(endTime, DateUtils.DATE_TIME_PATTERN_ONE);
            HbaseSearchData hbaseSearchData = new HbaseSearchData(deviceId, startFormateTime, endFormateTime);
            accidentModel.setMarkTime(endTime);
            mark = mark + 1;
            accidentModel.setMark(mark);
            accidentModel.setSystemTimeMarkTime(System.currentTimeMillis());
            return hbaseSearchData;
        }
    }

    /**
     * 处理事故点,做事故点的标注
     */
    public static boolean dealPoint(List<BaseDataPointWithSpeed> list, long time) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            Long times = list.get(i).getTime();
            if (times > time) {
                if (list.get(i).getSpeed() > 1) {
                    count++;
                }
            }
        }

        // 只要有两个速度大于1km/h的点我们就救人这个车没有停（当然这个没有标准，目前是自己定义的，后期完善）
        if (count > 2) {
            return false;
        }
        return true;
    }
}
