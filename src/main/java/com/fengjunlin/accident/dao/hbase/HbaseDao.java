package com.fengjunlin.accident.dao.hbase;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengjunlin.accident.model.BaseDataPointWithSpeed;
import com.fengjunlin.accident.model.Gps;
import com.fengjunlin.accident.model.HbaseSearchData;
import com.fengjunlin.accident.tools.constants.HbaseQueryConstants;
import com.fengjunlin.accident.tools.constants.TripKeyConstants;
import com.fengjunlin.accident.utils.DateUtils;
import com.fengjunlin.accident.utils.LogManager;
import com.fengjunlin.accident.utils.PositionUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;

/**
 * @Description Hbase的相关操作
 * @Author fengjl
 * @Date 2019/6/26 10:19
 * @Version 1.0
 **/
@SuppressWarnings("all")
@Component
public class HbaseDao {
    private static final Logger logger = LoggerFactory.getLogger(HbaseDao.class);

    @Autowired
    private HbaseConfig hbaseConfig;

    /**
     * 按照查询的时间去Hbase中查询成为OBD经纬度和时间数据(Gps数据)
     * GPS列簇为：trail
     * 经度：longitude
     * 维度：latitude
     * 经纬度类型：WGS-84
     * 时间：time 2018-09-06 12:36:40.000
     * zipData:(前29s的数据)
     *
     * @param hbaseSearchData
     * @return 模型的基础数据点
     */
    public List<BaseDataPointWithSpeed> getDataFromChengWeiOBDTableWithSpeed(HbaseSearchData hbaseSearchData) {
        Table table = null;
        List<BaseDataPointWithSpeed> list = new ArrayList<>(16);
        try {
            table = hbaseConfig.getHbaseConnection().getTable(TableName.valueOf(HbaseQueryConstants.CHENGWEI_TABLE_NAME));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(HbaseQueryConstants.CHENGWEI_ROW_KEY_PREFIX + hbaseSearchData.getDeviceId() + "_" + hbaseSearchData.getStartTime()));
            scan.setStopRow(Bytes.toBytes(HbaseQueryConstants.CHENGWEI_ROW_KEY_PREFIX + hbaseSearchData.getDeviceId() + "_" + hbaseSearchData.getEndTime()));
            FamilyFilter familyFilter = new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(HbaseQueryConstants.CHENGWEI_GPS_FAMILY_NAME.getBytes()));
            scan.setFilter(familyFilter);
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result res : resultScanner) {
                Map<String, String> map = new HashMap<>(16);
                for (Cell cell : res.rawCells()) {
                    String qualifier = new String(CellUtil.cloneQualifier(cell));
                    String cloneValue = new String(CellUtil.cloneValue(cell));
                    switch (qualifier) {
                        case "zipData":
                            if (StringUtils.isNotEmpty(cloneValue) && !cloneValue.equals("null")) {
                                map.put(qualifier, cloneValue);
                            }
                            break;
                        // 查询已经的点火的数据
                        case "accStatus":
                            if (StringUtils.isNotEmpty(cloneValue) && !cloneValue.equals("null") && cloneValue.equals("1")) {
                                map.put(qualifier, cloneValue);
                            }
                            break;
                        // 查询已经定位的数据
                        case "positionStatus":
                            if (StringUtils.isNotEmpty(cloneValue) && !cloneValue.equals("null") && cloneValue.equals("1")) {
                                map.put(qualifier, cloneValue);
                            }
                            break;
                        default:
                            break;
                    }
                    // 已经查询出来了，就结束循环做优化处理
                    if (map.size() == 3) {
                        break;
                    }
                }
                // 将map中的数据转换为BaseDataPoint对象，统一经纬度格式和时间格式
                List<BaseDataPointWithSpeed> baseDataPoints = loadChengWeiBaseDataPointWithSpeed(map);
                if (baseDataPoints.size() != 0 || baseDataPoints != null) {
                    list.addAll(baseDataPoints);
                }
            }
            Collections.sort(list);
            logger.info("****************  " + HbaseQueryConstants.CHENGWEI_TABLE_NAME + " 轨迹数据点排序成功  轨迹数据点的个数为：+" + list.size() + " ****************");
            return list;
        } catch (Exception e) {
            LogManager.error(e, this.getClass());
            logger.error("**************** " + HbaseQueryConstants.CHENGWEI_TABLE_NAME + " 查询hbase失败 ****************");
            return null;
        } finally {
            if (null != table) {
                try {
                    table.close();
                } catch (IOException e) {
                    LogManager.error(e, this.getClass());
                    return null;
                }
            }
        }

    }

    /**
     * 加载模型的基础数据点
     * 经度：longitude
     * 维度：latitude
     * 前29秒数据：zipData
     * 时间：time 2018-09-06 12:36:40.000
     * 经纬度类型：WGS-84
     *
     * @param map map hbase 中查询出来的数据点
     * @return BaseDataPoint or null
     */
    private List<BaseDataPointWithSpeed> loadChengWeiBaseDataPointWithSpeed(Map<String, String> map) {
        List<BaseDataPointWithSpeed> list = new ArrayList<>(16);

        String zipData = map.get(TripKeyConstants.ZIPDATA.getKey());

        try {
            JSONArray jsonArray = JSONArray.parseArray(zipData);
            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String time = jsonObject.getString(TripKeyConstants.TIME.getKey());
                    Double lng = jsonObject.getDouble(TripKeyConstants.LNG.getKey());
                    Double lat = jsonObject.getDouble(TripKeyConstants.LAT.getKey());
                    Double speed = jsonObject.getDouble(TripKeyConstants.SPEED.getKey());
                    if (lng > 20 && lat > 20) {
                        BaseDataPointWithSpeed baseDataPoint = paseDataWithSpeed(lng.toString(), lat.toString(), time, speed);
                        list.add(baseDataPoint);
                    }
                } catch (Exception e) {
                    logger.error("成为压缩数据包解压异常：{}" + zipData);
                    continue;
                }
            }

        } catch (Exception e) {
            logger.error("成为zip压缩包的数据不是数组");
        }
        return list;
    }

    /**
     * 主要用于解析成为的数据
     *
     * @param lng  经度
     * @param lat  维度
     * @param time 时间
     * @return 模型数据点
     */
    private BaseDataPointWithSpeed paseDataWithSpeed(String lng, String lat, String time, double speed) {
        try {
            // 坐标转换成为高德坐标
            Gps gps = PositionUtil.gps84_To_Gcj02(lat, lng);
            Long longTime = DateUtils.getLongTime(time, DateUtils.DATE_TIME_PATTERN);
            BaseDataPointWithSpeed baseDataPoint = new BaseDataPointWithSpeed(gps.getWgLon(), gps.getWgLat(), longTime, speed);
            return baseDataPoint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据设备类型获取表名字
     * 设备类型 0：歌途云镜，1：MIDAS，2：成为OBD，3：两客一危（jt808）
     *
     * @param deviceType 设备类型
     * @return 设备类型对应的表名
     */
    private String getTableNameByDeviceType(String deviceType) {
        String tableName = null;
        switch (deviceType) {
            case "0":
                tableName = "gtInfo_test";
                break;
            case "1":
                tableName = "device_mbidas_info";
                break;
            case "2":
                tableName = "device_chengwei_info";
                break;
            case "3":
                tableName = "device_jt808_info";
                break;
            default:
                tableName = null;
                break;
        }
        return tableName;
    }
}
