package com.dodal.meet.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum ValueType {

    CATEGORY("CATEGORY", "카테고리"),
    TAG("TAG", "태그"),
    ;

    @Getter
    private final String code;

    @Getter
    private final String description;

}
