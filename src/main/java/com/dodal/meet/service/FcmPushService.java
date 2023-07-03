package com.dodal.meet.service;


import com.dodal.meet.controller.request.fcm.FcmPushRequest;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.TokenEntityRepository;
import com.dodal.meet.repository.UserEntityRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmPushService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserEntityRepository userEntityRepository;
    private final TokenEntityRepository tokenEntityRepository;

    @Transactional(readOnly = true)
    public void sendFcmPushAllUsers(final FcmPushRequest request) {
        final String title = request.getTitle();
        final String body = request.getBody();

        List<UserEntity> entities = userEntityRepository.findAll();
        entities.stream().map(entity -> tokenEntityRepository.findById(entity.getId())
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_TOKEN_INFO))
                        .getFcmToken()).filter(Objects::nonNull).forEach(fcmToken -> sendFcmPush(title, body, fcmToken));
    }

    private void sendFcmPush(final String title, final String body, final String fcmToken) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .build();
        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            log.error("FCM PUSH ERROR : {}", e.getMessage());
            throw new DodalApplicationException(ErrorCode.FCM_PUSH_ERROR);
        }
    }
}
