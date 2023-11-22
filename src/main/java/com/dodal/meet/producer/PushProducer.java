package com.dodal.meet.producer;


import com.dodal.meet.controller.request.fcm.FcmKafkaPush;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushProducer {

    private final KafkaTemplate<String, FcmKafkaPush> kafkaTemplate;
    @Value("${spring.kafka.topic.push}")
    private String topic;

    public void send(FcmKafkaPush fcmKafkaPush) {
        kafkaTemplate.send(topic, fcmKafkaPush);
    }

}
