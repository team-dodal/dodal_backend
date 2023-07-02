package com.dodal.meet.controller.request.user;

import com.dodal.meet.model.SocialType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
@Getter
@Schema(description = "유저 소셜 회원가입 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignUpRequest {

    @Schema(description = "소셜 타입", allowableValues = {"KAKAO", "GOOGLE", "APPLE"}, example = "KAKAO")
    private SocialType socialType;

    @Schema(description = "소셜 아이디", example = "2843361325")
    private String socialId;

    @Schema(description = "이메일", example = "sasca37@naver.com")
    private String email;

    @Schema(description = "닉네임", example = "노래하는 어피치")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "S3 이미지 URL")
    private String profileUrl;

    @Schema(description = "한 줄 소개", example = "안녕하세요")
    private String content;

    @Schema(description = "관심 카테고리", example = "[\"001001\", \"002003\", \"004001\" ]")
    private List<String> tagList;
}
