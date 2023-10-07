package com.dodal.meet.controller.response.challengemanage;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@Schema(description = "도전방 멤버 관리 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChallengeUserInfoResponse {
    @Schema(description = "도전방 시퀀스 번호", example = "1")
    private Integer challengeRoomId;

    @Schema(description = "유저 시퀀스 번호", example = "1")
    private Long userId;

    @Schema(description = "유저 닉네임", example = "노래하는 어피치")
    private String nickname;

    @Schema(description = "유저 프로필 이미지", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String profileUrl;

    @Schema(description = "유저 주간 인증 성공 수", example = "3")
    private int certSuccessCnt;

    @Schema(description = "유저 주간 인증 실패 수", example = "1")
    private int certFailCnt;

//    @Schema(description = "유저 주간 정보", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private List<UserWeekCertInfo> userWeekCertInfoList;

    @QueryProjection
    public ChallengeUserInfoResponse(Integer challengeRoomId, Long userId, String nickname, String profileUrl) {
        this.challengeRoomId = challengeRoomId;
        this.userId = userId;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }
}
