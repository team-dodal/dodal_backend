package com.dodal.meet.service;


import com.dodal.meet.controller.request.fcm.FcmPushRequest;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.UserEntityRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.util.StringUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmPushService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserEntityRepository userEntityRepository;

    @Transactional(readOnly = true)
    public void sendFcmPushUser(final Long receiveUserId, final FcmPushRequest fcmPushRequest) {
        UserEntity userEntity = userEntityRepository.findById(receiveUserId).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        final String fcmToken = userEntity.getTokenEntity().getFcmToken();
        if (hasText(fcmToken)) {
            sendFcmPush(fcmPushRequest.getTitle(), fcmPushRequest.getBody(), userEntity.getTokenEntity().getFcmToken());
        } else {
            // 2023.09.29 : FCM 정보가 잘못되어도 연관된 다른 비즈니스 로직은 수행할 수 있도록 throw로 예외를 던지지 않는다.
//            throw new DodalApplicationException(ErrorCode.NOT_FOUND_FCM_TOKEN_INFO);
        }
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
            // 2023.09.29 : FCM 정보가 잘못되어도 연관된 다른 비즈니스 로직은 수행할 수 있도록 throw로 예외를 던지지 않는다.
            // throw new DodalApplicationException(ErrorCode.FCM_PUSH_ERROR);
        }
    }


}
