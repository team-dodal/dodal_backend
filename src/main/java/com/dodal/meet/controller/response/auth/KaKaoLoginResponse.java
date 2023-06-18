package com.dodal.meet.controller.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KaKaoLoginResponse {

    private long id;

    @JsonProperty("kakao_account")
    private KaKaoAccount kaKaoAccount;


    @Getter
    @Setter
    public static class KaKaoAccount {

        @JsonProperty("has_email")
        private String hasEmail;

        @JsonProperty("email_needs_agreement")
        private String emailNeedsAgreement;


        @JsonProperty("is_email_valid")
        private String isEmailValid;


        @JsonProperty("is_email_verified")
        private String isEmailVerified;

        private String email;
    }
}

