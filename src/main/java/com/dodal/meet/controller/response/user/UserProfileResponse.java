package com.dodal.meet.controller.response.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 프로필 이미지 등록 응답")
public class UserProfileResponse {

    @JsonProperty
    private String profileUrl;
}
