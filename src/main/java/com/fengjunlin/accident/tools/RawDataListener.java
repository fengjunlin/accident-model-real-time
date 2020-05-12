package com.fengjunlin.accident.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengjunlin.accident.service.ZipDataDataHander;
import com.fengjunlin.accident.tools.constants.KafKaConstant;
import com.fengjunlin.accident.tools.constants.TripKeyConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Author fengjl
 * @Date 2019/6/28 21:41
 * @Version 1.0
 **/
@Component
public class RawDataListener {

    private static final Logger logger = LoggerFactory.getLogger("KAFKA_LOGS");

    @Autowired
    private KafkaProducerTool kafkaProducerTool;

    @Autowired
    private ZipDataDataHander zipDataDataHander;

    /**
     * 批量消费kafka中的数据
     *
     * @throws IOException
     */
    @KafkaListener(topics = {"${spring.kafka.consumer.topic}"}, containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<String, String>> records) throws IOException {
        for (int i = 0; i < records.size(); i++) {
            try {
                ConsumerRecord<String, String> record = records.get(i);
                String key = record.key();
                String value = record.value();
                if (KafKaConstant.TRAIL.equals(key)) {
                    JSONObject trailJson = JSON.parseObject(value);
                    String imei = trailJson.getString(TripKeyConstants.IMEI.getKey());
                    String accStatus = trailJson.getString(TripKeyConstants.ACCSTATUS.getKey());
                    String positionStatus = trailJson.getString(TripKeyConstants.POSITIONSTATUS.getKey());
                    if (accStatus.equals(TripKeyConstants.STATUS_SUCCESS.getKey()) && positionStatus.equals(TripKeyConstants.STATUS_SUCCESS.getKey())) {
                        JSONArray zipData = trailJson.getJSONArray(TripKeyConstants.ZIPDATA.getKey());
                        if (zipData != null && zipData.size() != 0) {
                            zipDataDataHander.zipDataHander(imei, zipData);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("**************** 成为 kafka basic 数据处理异常");
                e.printStackTrace();
            }
        }
    }

}
