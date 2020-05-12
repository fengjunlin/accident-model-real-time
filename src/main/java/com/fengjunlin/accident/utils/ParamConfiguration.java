package com.fengjunlin.accident.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 读取项目json的配置信息
 * @Author fengjl
 * @Date 2019/4/15 12:37
 * @Version 1.0
 **/
public class ParamConfiguration {

    public static JSONObject configration;

    static {
        configration = ReadFile.getConfigData("configuration.json");
    }

    public static void main(String[] args) {
        String timeFormat = configration.getString("timeFormat");
        System.out.println(timeFormat);

    }

}
