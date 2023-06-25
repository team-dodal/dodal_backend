package com.dodal.meet.controller.response;


import com.dodal.meet.controller.response.user.UserSignInResponse;
import com.dodal.meet.controller.response.user.UserSignUpResponse;
import com.dodal.meet.exception.ErrorCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "공통 응답 처리")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Response<T> {

    @Schema(description = "응답 코드", example = "공통 응답 코드")
    private String resultCode;

    @Schema(description = "응답 메시지", example = "공통 응답 메시지", anyOf = {UserSignUpResponse.class, UserSignInResponse.class})
    private T result;

    public static Response<Void> success() {
        return new Response<>("SUCCESS", null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }

    public String toStream() {
        if (result == null) {
            return "{" +
                    "\"resultCode\":" + "\"" + resultCode + "\"," +
                    "\"result\":"  + null + "}";
        }
        return "{" +
                "\"resultCode\":" + "\"" + resultCode + "\"," +
                "\"result\":" + "\"" + result+ "\"" + "}";
    }
}
