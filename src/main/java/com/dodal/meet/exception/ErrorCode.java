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
    INVALID_REQUEST_FIELD(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다."),
    INVALID_TAG_LIST_FIELD(HttpStatus.BAD_REQUEST, "태그 정보가 올바르지 않습니다."),
    INVALID_SOCIAL_TYPE_FIELD(HttpStatus.BAD_REQUEST, "소셜 타입 정보가 올바르지 않습니다"),
    INVALID_NICKNAME_FIELD(HttpStatus.BAD_REQUEST, "닉네임은 공백으로 이루어질 수 없습니다"),
    INVALID_USER_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 유저 정보입니다."),
    ILLEGAL_IMAGE_REQUEST(HttpStatus.BAD_REQUEST, "이미지 URL과 이미지 파일을 동시에 요청할 수 없습니다"),
    INVALID_IMAGE_REQUEST(HttpStatus.BAD_REQUEST, "이미지 형식이 잘못되었습니다."),
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "등록된 이미지와 요청한 이미지 URL이 다릅니다."),
    INVALID_ROOM_SEARCH_TYPE(HttpStatus.BAD_REQUEST, "도전방 조회 요청 타입이 올바르지 않습니다."),
    INVALID_ROOM_JOIN(HttpStatus.BAD_REQUEST, "이미 도전방에 가입된 회원입니다."),
    INVALID_ROOM_LEAVE(HttpStatus.BAD_REQUEST, "도전방에 가입되어있지 않습니다."),

    BOOKMARK_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "북마크가 이미 등록되어 있습니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지 요청 용량은 1MB 이하여야 합니다."),
    INVALID_DAY_CODE(HttpStatus.BAD_REQUEST, "요일 코드가 올바르지 않습니다."),

    FEED_ALREADY_REQUEST(HttpStatus.BAD_REQUEST, "피드 인증 요청 중이거나 인증 완료 상태입니다."),
    INVALID_FEED_LIKE_REQUEST(HttpStatus.BAD_REQUEST, "요청한 사용자는 좋아요를 누른 상태입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "요청한 날짜 형식이 올바르지 않습니다"),
    INVALID_RANK_CODE(HttpStatus.BAD_REQUEST, "요청한 랭크 코드 정보가 올바르지 않습니다."),
    INVALID_YN_REQUEST(HttpStatus.BAD_REQUEST, "YN 필드 값은 Y 또는 N 이어야 합니다."),

    INVALID_USER_KICK_OUT(HttpStatus.BAD_REQUEST, "방장이 자기 자신을 강퇴할 수 없습니다."),
    INVALID_USER_ACCUSE(HttpStatus.BAD_REQUEST, "자기 자신을 신고할 수 없습니다."),

    INVALID_ACCUSE_REQUEST(HttpStatus.BAD_REQUEST, "신고 요청 정보가 잘못 되었습니다. 기타에는 내용이 필수이며, 나머지는 내용이 없어야 합니다."),
    ALREADY_ACCUSE_SUCCEED(HttpStatus.BAD_REQUEST, "이미 신고한 사용자입니다."),

    // 401 - UNAUTHORIZED
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "기간이 만료된 토큰 입니다."),

    UNAUTHORIZED_COMMENT(HttpStatus.UNAUTHORIZED, "댓글에 대한 권한이 없습니다."),
    UNAUTHORIZED_ROOM_HOST(HttpStatus.UNAUTHORIZED, "해당 사용자는 방장 권한이 없습니다"),

    // 404 - NOT FOUND
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND, "북마크가 등록되어 있지 않습니다"),
    NOT_FOUND_ROOM(HttpStatus.NOT_FOUND, "요청한 도전방이 존재하지 않습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "카테고리 정보가 존재하지 않습니다."),
    NOT_FOUND_ROOM_USER(HttpStatus.NOT_FOUND, "도전방에 사용자가 가입되어 있지 않습니다."),
    NOT_FOUND_ROOM_NOTI(HttpStatus.NOT_FOUND, "도전방에 요청한 공지사항이 존재하지 않습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글 ID가 존재하지 않습니다."),
    NOT_FOUND_FEED(HttpStatus.NOT_FOUND, "요청한 피드 정보가 존재하지 않습니다."),
    NOT_FOUND_FEED_LIKE(HttpStatus.NOT_FOUND, "요청한 피드 좋아요 정보가 존재하지 않습니다."),
    NOT_FOUND_TOKEN_INFO(HttpStatus.NOT_FOUND, "사용자 정보는 있으나, 토큰 정보가 없습니다."),
    NOT_FOUND_FCM_TOKEN_INFO(HttpStatus.NOT_FOUND, "사용자 정보는 있으나, FCM 토큰 정보가 없습니다."),
    NOT_FOUND_JWT_TOKEN(HttpStatus.NOT_FOUND, "JWT 토큰 정보가 없습니다."),
    NOT_FOUND_FCM_TOKEN(HttpStatus.NOT_FOUND, "FCM 토큰 정보가 없습니다."),
    NOT_FOUND_ROOM_HOST_USER(HttpStatus.NOT_FOUND, "도전방 방장 정보가 없습니다"),
    NOT_FOUND_TAG(HttpStatus.NOT_FOUND, "태그 정보가 서버에 존재하지 않습니다."),
    NOT_FOUND_ACCUSE_CODE(HttpStatus.NOT_FOUND, "존재하지 않는 신고 코드입니다."),
    NOT_FOUND_WORD(HttpStatus.NOT_FOUND, "검색어가 비어 있습니다."),

    // 500 - INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    FCM_PUSH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM PUSH 알림 발송 중 오류가 발생했습니다."),

    COMMON_CODE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 등록된 공통코드 정보를 찾을 수 없습니다.")
    ;



    private HttpStatus status;
    private String message;
}
