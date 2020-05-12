package com.fengjunlin.accident.config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description kafka消费者配置文件
 * @Author fengjl
 * @Date 2019/6/28 21:17
 * @Version 1.0
 **/
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.consumer.servers}")
    private String servers;

    @Value("${spring.kafka.consumer.enable.auto.commit}")
    private boolean enableAutoCommit;

    @Value("${spring.kafka.consumer.session.timeout}")
    private String sessionTimeout;

    @Value("${spring.kafka.consumer.auto.commit.interval}")
    private String autoCommitInterval;

    @Value("${spring.kafka.consumer.group.id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto.offset.reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.concurrency}")
    private int concurrency;

    @Value("${spring.kafka.consumer.offset.commit.type}")
    private String offsetCommitType;

    @Value("${spring.kafka.consumer.offset.commit.time}")
    private Long offsetCommitTime;

    @Value("${spring.kafka.consumer.offset.commit.count}")
    private int offsetCommitCount;

    @Value("${spring.kafka.consumer.batch.falg}")
    private boolean batchFalg ;

    @Value("${spring.kafka.consumer.batch.size}")
    private int batchSize ;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(1500);
        return factory;
    }
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        if(batchSize >0 && batchFalg ==true) {
            propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, batchSize);//每一批数量
        }
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return propsMap;
    }

    /**
     *  消费者批量
     */
    @Bean
    public KafkaListenerContainerFactory<?> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));
        //设置为批量消费
        if(batchSize >0 && batchFalg ==true) {
            factory.setBatchListener(batchFalg);
            factory.getContainerProperties().setAckMode(KafkaOffsetAckMode.getAckMode(offsetCommitType,offsetCommitTime,offsetCommitCount,factory.getContainerProperties()));
        }
        return factory;
    }
}
