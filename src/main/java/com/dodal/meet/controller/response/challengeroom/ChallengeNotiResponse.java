package com.dodal.meet.controller.response.challengeroom;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Schema(description = "도전방 공지사항 정보 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChallengeNotiResponse {

    @Schema(description = "공지사항 시퀀스", example = "1")
    private Integer notiId;

    @Schema(description = "방 시퀀스", example = "5")
    private Integer roomId;

    @Schema(description = "공지사항 제목", example = "주요 공지사항")
    private String title;

    @Schema(description = "공지 내용", example = "공지 내용입니다.")
    private String content;

    @Schema(description = "공지 일자", example = "2023.08.15 오후 10:17:31")
    private String date;

    @QueryProjection
    public ChallengeNotiResponse(Integer notiId, Integer roomId, String title, String content, String date) {
        this.notiId = notiId;
        this.roomId = roomId;
        this.title = title;
        this.content = content;
        this.date = date;
    }
}
