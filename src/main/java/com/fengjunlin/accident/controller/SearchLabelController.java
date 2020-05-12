package com.fengjunlin.accident.controller;

import com.fengjunlin.accident.tools.BaseDataAndAccidentCache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @Description 查询设备对应的标签数据
 * @Author fengjl
 * @Date 2019/7/3 11:08
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api")
public class SearchLabelController {

    @RequestMapping("/test")
    public Object searchLabelWithDeviceId() {
        return BaseDataAndAccidentCache.accidentList;
    }


}
