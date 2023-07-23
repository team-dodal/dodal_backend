package com.dodal.meet.controller.response.challenge;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@Schema(description = "도전 방 생성 응답")
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChallengeCreateResponse {

    @Schema(description = "도전방 시퀀스 번호", example = "1")
    private Integer challengeRoomId;

    @Schema(description = "유저 시퀀스 번호", example = "1")
    private Long userId;

    @Schema(description = "방장 닉네임", example = "1")
    private String nickname;

    @Schema(description = "도전방 제목", example = "[주 2회] 헬스 인증")
    private String title;

    @Schema(description = "도전방 소개글", example = "3대 200이상만")
    private String content;

    @Schema(description = "썸네일 이미지", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com/9a608042-ca04-47a4-8ad4-f5b3b8f7b2dc..ico")
    private String thumbnailImgUrl;

    @Schema(description = "모집 인원", example = "15")
    private int recruitCnt;

    @Schema(description = "인증 빈도수", example = "3")
    private int certCnt;

    @Schema(description = "인증 방법 소개글", example = "이미지엔 헬스장이 포함되어야 합니다.")
    private String certContent;

    @Schema(description = "올바른 인증 예시 이미지", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com/9a608042-ca04-47a4-8ad4-f5b3b8f7b2dc..ico")
    private String certCorrectImgUrl;

    @Schema(description = "잘못된 인증 예시 이미지", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com/9a608042-ca04-47a4-8ad4-f5b3b8f7b2dc..ico")
    private String certWrongImgUrl;

    @Schema(description = "북마크 수", example = "0")
    private int bookmarkCnt;

    @Schema(description = "주의사항", example = "주 5회 인증 미달 시 바로 강퇴합니다.")
    private String warnContent;

    @Schema(description = "신고 횟수", example = "0")
    private int accuseCnt;

    @Schema(description = "사용자 수", example = "1")
    private int userCnt;

    @Schema(description = "공지사항", example = "")
    private String noticeContent;

    @Schema(description = "도전방 생성 시간", example = "2023-07-15 18:58:51.056899")
    private Timestamp registeredAt;

    @Schema(description = "카테고리명", example = "건강")
    private String categoryName;

    @Schema(description = "카테고리 값", example = "001")
    private String categoryValue;

    @Schema(description = "태그명", example = "피트니스센터")
    private String tagName;

    @Schema(description = "태그 값", example = "001004")
    private String tagValue;


}
