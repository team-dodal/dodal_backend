package com.dodal.meet.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum SocialType {

    KAKAO("KAKAO"), GOOGLE("GOOGLE"), APPLE("APPLE")
    ;

    private final String value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SocialType findByValue(String value) {
        return Stream.of(SocialType.values())
                .filter(v -> v.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
