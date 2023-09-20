package com.dodal.meet.controller.response.feed;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@Schema(description = "피드 리스트 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class FeedResponse {

    // roomEntity
    @Schema(description = "도전방 시퀀스", example = "1")
    private Integer roomId;

    @Schema(description = "도전방 제목", example = "도달 도전방입니다.")
    private String title;

    @Schema(description = "피드 시퀀스", example = "3")
    private Long feedId;

    @Schema(description = "주간 인증 횟수", example = "3")
    private int certCnt;

    // tagEntity
    @Schema(description = "카테고리명", example = "건강")
    private String categoryName;

    // userEntity
    @Schema(description = "유저 시퀀스", example = "10")
    private Long userId;

    @Schema(description = "유저 닉네임", example = "노래하는 어피치")
    private String nickname;

    // challengeUserEntity
    @Schema(description = "유저 연속 인증 횟수", example = "10")
    private int continueCertCnt;

    // feedEntity
    @Schema(description = "인증 이미지 URL")
    private String certImgUrl;

    @Schema(description = "인증 내용", example = "반차쓰고 인증합니다.")
    private String certContent;

    @Schema(description = "좋아요 수", example = "5")
    private int likeCnt;

    @Schema(description = "신고 횟수", example = "1")
    private int accuseCnt;

    @Schema(description = "유저 좋아요 클릭 여부", example = "Y")
    private String likeYN;

    @Schema(description = "등록일", example = "20230819")
    private String registeredDate;

    @Schema(description = "등록 시간", example = "2023-08-19 15:00:12.056899")
    private Timestamp registeredAt;

    @QueryProjection
    public FeedResponse(Integer roomId, String title, Long feedId, int certCnt, String categoryName, Long userId, String nickname, int continueCertCnt, String certImgUrl, String certContent, int likeCnt, int accuseCnt, String likeYN, String registeredDate, Timestamp registeredAt) {
        this.roomId = roomId;
        this.title = title;
        this.feedId = feedId;
        this.certCnt = certCnt;
        this.categoryName = categoryName;
        this.userId = userId;
        this.nickname = nickname;
        this.continueCertCnt = continueCertCnt;
        this.certImgUrl = certImgUrl;
        this.certContent = certContent;
        this.likeCnt = likeCnt;
        this.accuseCnt = accuseCnt;
        this.likeYN = likeYN;
        this.registeredDate = registeredDate;
        this.registeredAt = registeredAt;
    }
}
