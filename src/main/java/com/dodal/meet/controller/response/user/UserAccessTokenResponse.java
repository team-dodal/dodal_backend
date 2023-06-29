package com.dodal.meet.controller.response.user;

import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.UserRole;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
@Schema(description = "유저 AccessToken 재발급 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserAccessTokenResponse {

    @Schema(description = "액세스 토큰 재발급", example = "액세스 토큰")
    private String accessToken;

}
