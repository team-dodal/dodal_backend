package com.dodal.meet.controller.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleLoginResponse {
    private String id;
    private String email;
    @JsonProperty("verified_email")
    private String verifiedEmail;

    private String picture;
}
