package com.dodal.meet.fixture;

import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.model.entity.UserTagEntity;

public class UserTagEntityFixture {

    public static UserTagEntity getUserTagEntity(final String tagName, final String tagValue, final UserEntity userEntity) {
        return UserTagEntity.builder()
                .userEntity(userEntity)
                .tagName(tagName)
                .tagValue(tagValue)
                .build();
    }
}
