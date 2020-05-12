package com.fengjunlin.accident.tools.constants;

/**
 * @Description 成为的轨迹数据 相关字段常量
 * @Author fengjl
 * @Date 2019/8/27 16:52
 * @Version 1.0
 **/
public enum TripKeyConstants {

    IMEI("imei", "成为轨迹数据的设备号"),
    ACCSTATUS("accStatus", "点火状态：0未点火，1已点火点火状态：0未点火，1已点火"),
    POSITIONSTATUS("positionStatus", "定位状态：0未定位，1已定位"),
    ZIPDATA("zipData", "成为轨迹压缩数据"),
    TIME("time", "成为轨迹压缩数据中的时间数据"),
    LNG("longitude", "成为轨迹压缩数据中经度数据"),
    LAT("latitude", "成为轨迹压缩数据中纬度数据"),
    SPEED("speed", "成为轨迹压缩数据中的速度"),
    STATUS_SUCCESS("1", "成为设备定位或者已经打火的状态值数据");

    private String key;

    private String description;

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    TripKeyConstants(String key, String description) {
        this.description = description;
        this.key = key;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"key\":\"")
                .append(key).append('\"');
        sb.append(",\"description\":\"")
                .append(description).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
