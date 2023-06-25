package com.dodal.meet.controller.response.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "유저 소셜 회원가입 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class UserSignUpResponse {

    @Schema(description = "자체 생성 액세스 토큰 - 5시간 뒤 만료", example = "서버 액세스 토큰")
    private String accessToken;

    @Schema(description = "자체 생성 리프레시 토큰 - 30일 뒤 만료", example = "서버 리프레시 토큰")
    private String refreshToken;
}
