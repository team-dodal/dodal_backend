package com.dodal.meet.controller.request.user;


import com.dodal.meet.model.SocialType;
import com.dodal.meet.valid.EnumValid;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Schema(description = "유저 소셜 로그인 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserSignInRequest {


    @EnumValid(enumClass = SocialType.class, message = "social_type은 KAKAO, GOOGLE, APPLE 형태로 요청해야 합니다.")
    @Schema(description = "소셜 타입", allowableValues = {"KAKAO", "GOOGLE", "APPLE"}, example = "KAKAO")
    private SocialType socialType;

    @NotBlank(message = "social_id는 필수 값입니다.")
    @Schema(description = "소셜 아이디", example = "2843361325")
    private String socialId;

}
