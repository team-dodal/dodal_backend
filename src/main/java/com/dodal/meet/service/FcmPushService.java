package com.dodal.meet.service;


import com.dodal.meet.controller.request.fcm.FcmPushRequest;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.TokenEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.TokenEntityRepository;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.utils.UserUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static org.springframework.util.StringUtils.*;

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
        entities.stream().map(entity -> tokenEntityRepository.findByUserEntity(entity)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FCM_TOKEN_INFO))
                        .getFcmToken()).filter(Objects::nonNull).forEach(fcmToken -> sendFcmPush(title, body, fcmToken));
    }

    @Transactional(readOnly = true)
    public void sendFcmPushUser(final Long receiveUserId, final FcmPushRequest fcmPushRequest) {
        UserEntity userEntity = userEntityRepository.findById(receiveUserId).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        final String fcmToken = userEntity.getTokenEntity().getFcmToken();
        if (!hasText(fcmToken)) {
            throw new DodalApplicationException(ErrorCode.NOT_FOUND_FCM_TOKEN_INFO);
        }
        sendFcmPush(fcmPushRequest.getTitle(), fcmPushRequest.getBody(), userEntity.getTokenEntity().getFcmToken());
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
