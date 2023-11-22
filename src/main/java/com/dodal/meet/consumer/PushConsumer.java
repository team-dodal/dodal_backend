package com.dodal.meet.consumer;


import com.dodal.meet.controller.request.fcm.FcmKafkaPush;
import com.dodal.meet.service.FcmPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class PushConsumer {

    private final FcmPushService fcmPushService;


    @KafkaListener(topics = "${spring.kafka.topic.push}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePush(FcmKafkaPush fcmKafkaPush) {
        fcmPushService.sendFcmPushUser(fcmKafkaPush.getUserId(), fcmKafkaPush.getFcmPushRequest());
    }
}
