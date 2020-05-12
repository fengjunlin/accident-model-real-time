package com.fengjunlin.accident.tools;

import com.fengjunlin.accident.model.AccidentModel;
import com.fengjunlin.accident.model.BaseDataPointWithSpeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 主要用于基础数据点和事故数据点缓存
 * @Author fengjl
 * @Date 2019/8/27 17:06
 * @Version 1.0
 **/

public class BaseDataAndAccidentCache {
    /**
     * 缓存每个设备号前30s有效的模型数据点
     */
    public static Map<String, List<BaseDataPointWithSpeed>> map = new ConcurrentHashMap<>(16);

    /**
     * 碰撞时刻的综合数据
     */
    public static List<AccidentModel> accidentList = Collections.synchronizedList(new ArrayList<AccidentModel>());

}
