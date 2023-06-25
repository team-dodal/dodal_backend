package com.dodal.meet.controller.request.user;

import com.dodal.meet.model.SocialType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
@Getter
@Schema(description = "유저 소셜 회원가입 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class UserSignUpRequest {

    @Schema(description = "소셜 타입", allowableValues = {"KAKAO", "GOOGLE", "APPLE"}, example = "KAKAO")
    private SocialType socialType;

    @Schema(description = "소셜 아이디", example = "2843361325")
    private String socialId;

    @Schema(description = "닉네임", example = "노래하는 어피치")
    private String nickname;

    @Schema(description = "관심 카테고리", example = "[\"001001\", \"002003\", \"004001\" ]")

    private List<Integer> favoriteCategory;

    @Schema(description = "FCM 토큰", example = "1238129389")
    private String fcmToken;
}
