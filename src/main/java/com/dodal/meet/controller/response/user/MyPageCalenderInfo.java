package com.dodal.meet.controller.response.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Schema(description = "마이페이지 도전방 월별 정보")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@ToString
public class MyPageCalenderInfo {

    @Schema(description = "피드 시퀀스", example = "3")
    private Long feedId;

    @Schema(description = "인증 이미지 url", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String certImageUrl;

    @Schema(description = "일자", example = "17")
    private String day;


    @QueryProjection
    public MyPageCalenderInfo(Long feedId, String certImageUrl, String day) {
        this.feedId = feedId;
        this.certImageUrl = certImageUrl;
        this.day = day;
    }
}
