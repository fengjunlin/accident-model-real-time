package com.fengjunlin.accident.config;

import com.google.common.collect.Maps;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 */
@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
   
    @Value("${spring.kafka.producer.acks}")
    private String kafkaAcks;

    @Value("${spring.kafka.producer.retries}")
    private int kafkaRetries;

    @Value("${spring.kafka.producer.batch-size}")
    private int kafkaBatchSize;

    @Value("${spring.kafka.producer.linger}")
    private int kafkaLinger;

    @Value("${spring.kafka.producer.buffer-memory}")
    private int kafkaBufferMemory;

    /**
     *  生产者配置信息
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = Maps.newHashMap();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, kafkaAcks);
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaRetries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaBatchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaLinger);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaBufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    /**
     *  生产者工厂
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     *  生产者模板
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
}