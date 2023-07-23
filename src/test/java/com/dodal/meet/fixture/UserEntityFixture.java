package com.dodal.meet.fixture;

import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity getUserEntity(final String socialId, final SocialType socialType) {
        return UserEntity.builder()
                .socialId(socialId)
                .socialType(socialType)
                .build();
    }
}