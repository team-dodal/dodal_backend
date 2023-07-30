package com.dodal.meet.utils;

import com.dodal.meet.controller.request.fcm.FcmPushRequest;

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
}
