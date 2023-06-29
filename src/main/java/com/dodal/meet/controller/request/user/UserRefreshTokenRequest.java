package com.dodal.meet.controller.request.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(description = "유저 AccessToken 재발급 요청")
@ToString
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRefreshTokenRequest {

    @Schema(description = "refreshToken 토큰 요청", example = "eyJhbGciOiJIUzI1NiJ9.eyJzb2NpYWxJZCI6IjI4NDMzNjEzMjUiLCJzb2NpYWxUeXBlIjoiS0FLQU8iLCJpYXQiOjE2ODc3ODUyMDQsImV4cCI6MTY5MDM3NzIwNH0.5v4ULCWCMoxfslJvPmJI21Si-eWJVEV_6ZLxGm6Cvv4")
    private String refreshToken;
}
