package com.dodal.meet.controller.request.fcm;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class FcmKafkaPush {
    private Long userId;
    private FcmPushRequest fcmPushRequest;


    public static FcmKafkaPush makeKafkaPush(Long userId, FcmPushRequest fcmPushRequest) {
        return FcmKafkaPush.builder().userId(userId).fcmPushRequest(fcmPushRequest).build();
    }
}
