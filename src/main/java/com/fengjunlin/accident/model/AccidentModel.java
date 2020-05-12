package com.fengjunlin.accident.model;

import java.util.List;

/**
 * @Description 主要用于封装事故数据
 * @Author fengjl
 * @Date 2019/8/28 14:20
 * @Version 1.0
 **/
public class AccidentModel {
    private String deviceId;
    /**
     * 前10s 钟的平均速度
     */
    private double averageSpeed;
    /**
     * 前10s 钟的最高速度
     */
    private  double maxSpeed;
    /**
     * 最低速度
     */
    private double minSpeed;
    /**
     * 判断了几次
     */
    private Integer mark;
    /**
     * 标记时间,用于定位每次hbase的查询的时间
     */
    private Long markTime;
    /**
     * 用于标记每次判断后的时间，下一次从什么时间开始,从什么时间开始
     */
    private Long markTimeTwo;
    /**
     * 用于清理的缓存中的疑似车辆的碰撞数据点
     */
    private Long SystemTimeMarkTime;
    /**
     * 检验发生碰撞的时候的模型数据点
     */
    private ModelDataPointWithSpeed modelDataPointWithSpeed;
    /**
     * 碰撞前10s钟的轨迹
     */
    private List<Gps> track;


    public AccidentModel() {
    }

    public AccidentModel(String deviceId, double averageSpeed, double maxSpeed, double minSpeed, ModelDataPointWithSpeed modelDataPointWithSpeed, List<Gps> track) {
        this.deviceId = deviceId;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.modelDataPointWithSpeed = modelDataPointWithSpeed;
        this.track = track;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Long getMarkTime() {
        return markTime;
    }

    public void setMarkTime(Long markTime) {
        this.markTime = markTime;
    }

    public Long getMarkTimeTwo() {
        return markTimeTwo;
    }

    public void setMarkTimeTwo(Long markTimeTwo) {
        this.markTimeTwo = markTimeTwo;
    }

    public Long getSystemTimeMarkTime() {
        return SystemTimeMarkTime;
    }

    public void setSystemTimeMarkTime(Long systemTimeMarkTime) {
        SystemTimeMarkTime = systemTimeMarkTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public ModelDataPointWithSpeed getModelDataPointWithSpeed() {
        return modelDataPointWithSpeed;
    }

    public void setModelDataPointWithSpeed(ModelDataPointWithSpeed modelDataPointWithSpeed) {
        this.modelDataPointWithSpeed = modelDataPointWithSpeed;
    }

    public List<Gps> getTrack() {
        return track;
    }

    public void setTrack(List<Gps> track) {
        this.track = track;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccidentModel)) return false;

        AccidentModel that = (AccidentModel) o;

        if (Double.compare(that.getAverageSpeed(), getAverageSpeed()) != 0) return false;
        if (Double.compare(that.getMaxSpeed(), getMaxSpeed()) != 0) return false;
        if (Double.compare(that.getMinSpeed(), getMinSpeed()) != 0) return false;
        if (getDeviceId() != null ? !getDeviceId().equals(that.getDeviceId()) : that.getDeviceId() != null)
            return false;
        if (getModelDataPointWithSpeed() != null ? !getModelDataPointWithSpeed().equals(that.getModelDataPointWithSpeed()) : that.getModelDataPointWithSpeed() != null)
            return false;
        return getTrack() != null ? getTrack().equals(that.getTrack()) : that.getTrack() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getDeviceId() != null ? getDeviceId().hashCode() : 0;
        temp = Double.doubleToLongBits(getAverageSpeed());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getMaxSpeed());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getMinSpeed());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getModelDataPointWithSpeed() != null ? getModelDataPointWithSpeed().hashCode() : 0);
        result = 31 * result + (getTrack() != null ? getTrack().hashCode() : 0);
        return result;
    }
}
