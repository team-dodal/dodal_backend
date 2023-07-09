package com.dodal.meet.controller.request.user;

import com.dodal.meet.model.SocialType;
import com.dodal.meet.valid.EnumValid;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.List;
@Getter
@Setter
@Schema(description = "유저 소셜 회원가입 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserSignUpRequest {

    @EnumValid(enumClass = SocialType.class, message = "social_type은 KAKAO, GOOGLE, APPLE 형태로 요청해야 합니다.")
    @Schema(description = "소셜 타입", allowableValues = {"KAKAO", "GOOGLE", "APPLE"}, example = "KAKAO")
    private SocialType socialType;

    @NotBlank(message = "social_id는 필수 값입니다.")
    @Schema(description = "소셜 아이디", example = "2843361325")
    private String socialId;

    @Email(message = "email 형식이 올바르지 않습니다.")
    @Schema(description = "이메일", example = "sasca37@naver.com")
    private String email;

    @Size(min = 1, max = 16, message = "nickname은 1자 이상 16자 이하여야합니다.")
    @Schema(description = "닉네임", example = "노래하는 어피치")
    private String nickname;

    @Schema(description = "Multipart/form-data 형식 이미지 업로드", example = "profile")
    private String profileUrl;

    @Length(max = 50, message = "content는 50자 이하여야합니다.")
    @Schema(description = "한 줄 소개", example = "안녕하세요")
    private String content;

    @NotNull
    @Schema(description = "관심 카테고리", example = "[\"001001\", \"002003\", \"004001\" ]")
    private List<String> tagList;
}
