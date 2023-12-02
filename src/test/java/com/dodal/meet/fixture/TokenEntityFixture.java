package com.dodal.meet.fixture;

import com.dodal.meet.model.entity.TokenEntity;

public class TokenEntityFixture {

    private static final String REFRESH_TOKEN = "default_refresh_token";
    private static final String FCM_TOKEN = "default_fcm_token";

    public static TokenEntity getTokenEntity() {
        return TokenEntity.builder().refreshToken(REFRESH_TOKEN).fcmToken(FCM_TOKEN).build();
    }
}
