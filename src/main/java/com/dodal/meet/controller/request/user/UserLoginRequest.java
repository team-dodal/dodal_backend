package com.dodal.meet.controller.request.user;


import com.dodal.meet.model.SocialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Schema(description = "유저 소셜 로그인 요청")
public class UserLoginRequest {
    @JsonProperty("social_type")
    @Schema(description = "소셜 타입", allowableValues = {"KAKAO", "GOOGLE", "APPLE"}, example = "KAKAO")
    private SocialType socialType;

    @JsonProperty("access_token")
    @Schema(description = "액세스 토큰", nullable = true, example = "최초 로그인 또는 서버 리프레시 토큰 만료 시 OAuth , 재 로그인 시 서버 토큰")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "리프레시 토큰", nullable = true, example = "서버 토큰")
    private String refreshToken;

}
