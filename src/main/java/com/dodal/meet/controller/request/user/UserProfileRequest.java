package com.dodal.meet.controller.request.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(description = "유저 프로필 이미지 등록 요청")
@ToString
public class UserProfileRequest {
    @Schema(description = "Multipart/form-data 형식 이미지 업로드", example = "profile")
    private MultipartFile profile;
}
