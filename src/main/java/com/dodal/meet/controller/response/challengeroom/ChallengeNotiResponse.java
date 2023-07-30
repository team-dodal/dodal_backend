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

    private Integer notiId;

    private Integer roomId;

    private String title;

    private String content;

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
