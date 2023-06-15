package com.dodal.meet.controller.request.user;


import com.dodal.meet.model.SocialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserLoginRequest {
    @JsonProperty("social_type")
    private SocialType socialType;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

}
