package com.dodal.meet.model;


import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
public enum
RoomSearchType {

    INTEREST("INTEREST", "관심있는 도전"),
    RECENCY("RECENCY", "최신 도전"),
    POPULARITY("POPULARITY", "인기있는 도전")
    ;

    private static final Map<String, String> CODE = Collections
            .unmodifiableMap(Stream.of(values()).collect(Collectors.toMap(RoomSearchType::getCode, RoomSearchType::name)));

    public static RoomSearchType of(final String code) {
        if (!StringUtils.hasText(CODE.get(code))) {
            throw new DodalApplicationException(ErrorCode.INVALID_ROOM_SEARCH_TYPE);
        }
        return RoomSearchType.valueOf(CODE.get(code));
    }

    public static boolean isValidSearchType(String code) {
        if (code == null) {
            return false;
        }
        for (RoomSearchType type : RoomSearchType.values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
    @Getter
    private final String code;

    @Getter
    private final String description;


}
