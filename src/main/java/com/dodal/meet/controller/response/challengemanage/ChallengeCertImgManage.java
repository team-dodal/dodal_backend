package com.dodal.meet.controller.response.challengemanage;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@Schema(description = "운영중인 도전방 피드 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@ToString
public class ChallengeCertImgManage {

    @Schema(description = "도전방 시퀀스 번호", example = "1")
    private Integer challengeRoomId;

    @Schema(description = "피드 시퀀스 번호", example = "1")
    private Long challengeFeedId;
    @Schema(description = "인증 요청한 유저 시퀀스 번호", example = "1")
    private Long requestUserId;

    @Schema(description = "인증 이미지 url", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String certImageUrl;

    @Schema(description = "인증 요청 내용", example = "오늘도 4시간 동안 프리다이빙 했어요.")
    private String certContent;

    @Schema(description = "인증 상태", example = "1")
    private String certCode;
    @Schema(description = "인증 요청 시간", example = "2023-07-15 18:58:51.056899")
    private Timestamp registeredAt;

    @Schema(description = "인증 요청 일자", example = "20230801")
    private String registeredDate;

    @QueryProjection

    public ChallengeCertImgManage(Integer challengeRoomId, Long challengeFeedId, Long requestUserId, String certImageUrl, String certContent, String certCode, Timestamp registeredAt, String registeredDate) {
        this.challengeRoomId = challengeRoomId;
        this.challengeFeedId = challengeFeedId;
        this.requestUserId = requestUserId;
        this.certImageUrl = certImageUrl;
        this.certContent = certContent;
        this.certCode = certCode;
        this.registeredAt = registeredAt;
        this.registeredDate = registeredDate;
    }
}
