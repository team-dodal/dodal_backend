package com.dodal.meet.controller.request.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Schema(description = "유저 FCM 토큰 업데이트 요청")
@ToString
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserFcmTokenRequest {

    @NotBlank(message = "fcm_token은 필수 값입니다.")
    @Schema(description = "FCM 토큰 요청", example = "239812371289")
    private String fcmToken;
}
