package com.dodal.meet.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomRole {
    USER("USER", "일반 사용자"),
    HOST("HOST", "방장");

    private final String key;
    private final String title;
}
