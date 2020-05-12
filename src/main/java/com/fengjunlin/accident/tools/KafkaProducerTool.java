package com.fengjunlin.accident.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fengjunlin.accident.tools.constants.KafKaConstant;
import com.fengjunlin.accident.utils.LogLevel;
import com.fengjunlin.accident.utils.LogManager;
import com.fengjunlin.accident.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @Description kafka 工具类
 * @Author fengjl
 * @Date 2019/6/28 20:04
 * @Version 1.0
 **/
@Component
public class KafkaProducerTool {

    private static final Logger logger = LoggerFactory.getLogger("KAFKA_LOGS");


    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 发送数据到kafka
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean sendMessageToKafka(String topic, String columnCluster, Object obj, String message) {
        if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(columnCluster) || obj == null
                || StringUtils.isEmpty(message)) {
            return false;
        }
        ListenableFuture<SendResult<String, String>> send = null;
        try {
            String jsonString = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
            send = kafkaTemplate.send(topic, new Random().nextInt(KafKaConstant.PARTITION_COUNT), columnCluster, jsonString);
            LogManager.normal(message + "kafka发送结果: " + send.get().getProducerRecord().value(), this.getClass(),
                    LogLevel.INFO);
            return send.isDone();
        } catch (Exception e) {
            logger.error("************ 成为 kafka发送行程数据异常 ************");
            LogManager.error(e, this.getClass());
        }
        return false;
    }


}
