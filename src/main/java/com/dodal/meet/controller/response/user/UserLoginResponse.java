package com.dodal.meet.controller.response.user;


import com.dodal.meet.model.SocialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
@Schema(description = "유저 소셜 로그인 응답")
public class UserLoginResponse {

    @JsonProperty("social_type")
    @Schema(description = "소셜 타입", allowableValues = {"KAKAO", "GOOGLE", "APPLE"}, example = "KAKAO")
    private SocialType socialType;

    @Schema(description = "소셜 이메일", example = "sasca37@naver.com")
    private String email;

    @JsonProperty("access_token")
    @Schema(description = "자체 생성 액세스 토큰 - 5시간 뒤 만료", example = "서버 액세스 토큰")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "자체 생성 리프레시 토큰 - 30일 뒤 만료", example = "서버 리프레시 토큰")
    private String refreshToken;
}
