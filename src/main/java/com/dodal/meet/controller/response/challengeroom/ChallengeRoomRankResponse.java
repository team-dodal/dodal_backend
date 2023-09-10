package com.dodal.meet.controller.response.challengeroom;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@Schema(description = "도전방 랭킹 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
@AllArgsConstructor
public class ChallengeRoomRankResponse {

    @Schema(description = "닉네임", example = "노래하는 어피치")
    private String nickname;

    @Schema(description = "프로필 이미지 url", example = "https://")
    private String profileUrl;

    @Schema(description = "연속 인증 횟수", example = "0")
    private Long certCnt;

}
