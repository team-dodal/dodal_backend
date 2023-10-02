package com.dodal.meet.utils;

import com.dodal.meet.controller.request.fcm.FcmPushRequest;
import com.dodal.meet.controller.response.alarm.AlarmHistResponse;

public class MessageUtils {


    public static FcmPushRequest makeFcmPushRequest(final MessageType messageType, final String roomTitle) {
        String title = roomTitle;
        String content = messageType.getDescription();

        return FcmPushRequest
                .builder()
                .title(title)
                .body(content)
                .build();
    }

    public static AlarmHistResponse makeAlarmHistResponse(final MessageType messageType, final String title, final Long userId, final int roomId) {
        return AlarmHistResponse
                .builder()
                .userId(userId)
                .roomId(roomId)
                .title(title)
                .content(messageType.getDescription())
                .registeredDate(DateUtils.getToday())
                .build();
    }
}
