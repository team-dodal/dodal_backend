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
    INVALID_REQUEST_FILED(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다."),

    INVALID_USER_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 유저 정보입니다."),

    INVALID_IMAGE_REQUEST(HttpStatus.BAD_REQUEST, "이미지 형식이 잘못되었습니다."),

    // 401 - UNAUTHORIZED
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 500 - INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    NOT_FOUND_TOKEN_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보는 있으나, 토큰 정보가 없습니다."),
    NOT_FOUND_JWT_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 토큰 정보가 없습니다."),
    NOT_FOUND_FCM_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 토큰 정보가 없습니다."),

    NOT_FOUND_TAG(HttpStatus.INTERNAL_SERVER_ERROR, "태그 정보가 서버에 존재하지 않습니다."),
    FCM_PUSH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM PUSH 알림 발송 중 오류가 발생했습니다.")
    ;



    private HttpStatus status;
    private String message;
}
