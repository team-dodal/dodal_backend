package com.dodal.meet.controller.response.user;


import com.dodal.meet.model.SocialType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
@Schema(description = "유저 소셜 로그인 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserSignInResponse {

    @Schema(description = "로그인 여부", example = "true")
    private String isSigned;

    @Schema(description = "자체 생성 액세스 토큰 - 5시간 뒤 만료", example = "서버 액세스 토큰")
    private String accessToken;

    @Schema(description = "자체 생성 리프레시 토큰 - 30일 뒤 만료", example = "서버 리프레시 토큰")
    private String refreshToken;
}
