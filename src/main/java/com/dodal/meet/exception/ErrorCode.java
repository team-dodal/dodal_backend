package com.dodal.meet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // 400 - BAD REQUEST
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 타입입니다."),
    INVALID_SIGNUP_REQUEST(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),

    INVALID_USER_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 유저 정보입니다."),

    INVALID_IMAGE_REQUEST(HttpStatus.BAD_REQUEST, "이미지 형식이 잘못되었습니다."),

    // 401 - UNAUTHORIZED
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 500 - INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    ;



    private HttpStatus status;
    private String message;
}
