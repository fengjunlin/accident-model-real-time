package com.fengjunlin.accident.model;

/**
 * @Description Hbase查询参数映射实体类
 * @Author fengjl
 * @Date 2019/6/27 14:07
 * @Version 1.0
 **/
public class HbaseSearchData {
    /**
     * 设备Id
     */
    private String deviceId;
    /**
     * 开始时间 时间格式为：
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;

    public HbaseSearchData() {
    }

    public HbaseSearchData(String deviceId, String startTime, String endTime) {
        this.deviceId = deviceId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HbaseSearchData)) return false;

        HbaseSearchData that = (HbaseSearchData) o;

        if (getDeviceId() != null ? !getDeviceId().equals(that.getDeviceId()) : that.getDeviceId() != null)
            return false;
        if (getStartTime() != null ? !getStartTime().equals(that.getStartTime()) : that.getStartTime() != null)
            return false;
        return getEndTime() != null ? getEndTime().equals(that.getEndTime()) : that.getEndTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getDeviceId() != null ? getDeviceId().hashCode() : 0;
        result = 31 * result + (getStartTime() != null ? getStartTime().hashCode() : 0);
        result = 31 * result + (getEndTime() != null ? getEndTime().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HbaseSearchData{" +
                "deviceId='" + deviceId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
