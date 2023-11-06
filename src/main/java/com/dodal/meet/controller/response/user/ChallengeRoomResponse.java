package com.dodal.meet.controller.response.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(description = "도전방 정보 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@ToString
public class ChallengeRoomResponse {

    @Schema(description = "도전방 시퀀스 번호", example = "1")
    private Integer roomId;

    @Schema(name =  "title", example = "매일매일 자격증 공부!")
    private String title;
}
