package com.dodal.meet.controller.response.challengemanage;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@Schema(description = "도전방 멤버 주간 인증 정보")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@ToString
public class UserWeekCertInfo {

    @Schema(description = "피드 시퀀스", example = "3")
    private Long feedId;

    @Schema(description = "인증 이미지 url", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String certImageUrl;

    @Schema(description = "인증 상태", example = "1")
    private String certCode;

    @Schema(description = "요일 코드", example = "0")
    private String dayCode;


    @QueryProjection
    public UserWeekCertInfo(Long feedId, String certImageUrl, String certCode, String dayCode) {
        this.feedId = feedId;
        this.certImageUrl = certImageUrl;
        this.certCode = certCode;
        this.dayCode = dayCode;
    }
}
