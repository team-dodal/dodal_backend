package com.dodal.meet.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum MessageType {

    REQUEST("REQUEST","도전방 인증 요청이 왔습니다."),
    CONFIRM("CONFIRM", "도전방 인증이 승인되었습니다."),
    REJECT("REJECT",  "인증 승인이 거절되었습니다. 재도전 해주세요."),
    ;

    @Getter
    private final String code;
    @Getter
    private final String description;

}