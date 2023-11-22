package com.dodal.meet.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class DodalProducer {

    private static final String TOPIC_NAME = "test";
    private static final String BOOTSTRAP_SERVERS = "43.200.139.227:9092";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // KafkaProducer<?,?> key, value 타입에 맞춰 직렬화
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageValue = "sasca37";
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);

        // ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, PARTITION_NUM, KEY, VALUE);

        Future<RecordMetadata> send = producer.send(record);
        RecordMetadata recordMetadata = send.get();
        log.info("@@@@ : {}", recordMetadata.toString());

        producer.flush();
        producer.close();
    }
}
