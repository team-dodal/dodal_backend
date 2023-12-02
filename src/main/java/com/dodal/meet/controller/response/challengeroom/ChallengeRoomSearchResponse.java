package com.dodal.meet.controller.response.challengeroom;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@Schema(description = "카테고리와 태그 정보 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@ToString
public class ChallengeRoomSearchResponse {
    @Schema(description = "도전방 시퀀스 번호", example = "1")
    private Integer challengeRoomId;

    @Schema(description = "방장 시퀀스 번호", example = "1")
    private Long hostId;

    @Schema(description = "방장 닉네임", example = "1")
    private String hostNickname;

    @Schema(description = "방장 프로필 이미지 URL", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String hostProfileUrl;

    @Schema(description = "도전방 제목", example = "[주 2회] 헬스 인증")
    private String title;

    @Schema(description = "도전방 소개글", example = "도전방 소개 글입니다.")
    private String content;

    @Schema(description = "인증 횟수", example = "2")
    private int certCnt;

    @Schema(description = "썸네일 이미지", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String thumbnailImgUrl;

    @Schema(description = "모집 인원", example = "15")
    private int recruitCnt;

    @Schema(description = "사용자 수", example = "1")
    private int userCnt;

    @Schema(description = "북마크 수", example = "1")
    private int bookmarkCnt;

    @Schema(description = "유저 북마크 여부", example = "N")
    private String bookmarkYN;

    @Builder.Default
    @Schema(description = "유저 가입 여부", example = "N")
    private String joinYN = "N";

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


    @QueryProjection
    public ChallengeRoomSearchResponse(Integer challengeRoomId, Long hostId, String hostNickname, String hostProfileUrl, String title, String content, int certCnt,
                                       String thumbnailImgUrl, int recruitCnt, int userCnt, int bookmarkCnt, String bookmarkYN, Timestamp registeredAt,
                                       String categoryName, String categoryValue, String tagName, String tagValue) {
        this.challengeRoomId = challengeRoomId;
        this.hostId = hostId;
        this.hostNickname = hostNickname;
        this.hostProfileUrl = hostProfileUrl;
        this.title = title;
        this.content = content;
        this.certCnt = certCnt;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.recruitCnt = recruitCnt;
        this.userCnt = userCnt;
        this.bookmarkCnt = bookmarkCnt;
        this.bookmarkYN = bookmarkYN;
        this.registeredAt = registeredAt;
        this.categoryName = categoryName;
        this.categoryValue = categoryValue;
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

}
