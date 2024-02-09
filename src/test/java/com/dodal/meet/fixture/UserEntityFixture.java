package com.dodal.meet.fixture;

import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.UserEntity;

public class UserEntityFixture {


    public static UserEntity getUserEntity(String socialId, final SocialType socialType) {
        return UserEntity.builder()
                .socialId(socialId)
                .socialType(socialType)
                .nickname(socialId + socialType.name())
                .tokenEntity(TokenEntityFixture.getTokenEntity())
                .build();
    }
}
