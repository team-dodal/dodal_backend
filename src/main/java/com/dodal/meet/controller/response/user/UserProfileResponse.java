package com.dodal.meet.controller.response.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(description = "유저 프로필 이미지 등록 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class UserProfileResponse {

    private String profileUrl;
}
