package com.dodal.meet.controller.request.user;


import com.dodal.meet.model.SocialType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "유저 소셜 로그인 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class UserSignInRequest {
    @Schema(description = "소셜 타입", allowableValues = {"KAKAO", "GOOGLE", "APPLE"}, example = "KAKAO")
    private SocialType socialType;

    @Schema(description = "소셜 아이디", example = "2843361325")
    private String socialId;

}
