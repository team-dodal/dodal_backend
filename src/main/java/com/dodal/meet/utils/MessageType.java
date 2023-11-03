package com.dodal.meet.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum MessageType {

    REQUEST("REQUEST","도전방 인증 요청이 왔습니다."),
    CONFIRM("CONFIRM", "도전방 인증이 승인되었습니다."),
    REJECT("REJECT",  "인증 승인이 거절되었습니다. 재도전 해주세요."),
    KICK_OUT("KICK_OUT", "도전방에서 강퇴 되었습니다."),
    MANDATE("MANDATE", "도전방 방장이 되었습니다.")
    ;

    @Getter
    private final String code;
    @Getter
    private final String description;

}
