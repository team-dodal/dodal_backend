package com.dodal.meet.controller.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "공통 응답 처리")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
@NoArgsConstructor
public class Response<T> {

    @Schema(description = "응답 코드", example = "공통 응답 코드")
    private String resultCode;

    @Schema(description = "응답 메시지")
    private T result;

    public static ResponseSuccess<Void> success() {
        return new ResponseSuccess<>("SUCCESS", null);
    }

    public static ResponseSuccess<Void> fail() {return new ResponseSuccess<>("FAIL", null);}

    public static <T> ResponseSuccess<T> success(T result) {
        return new ResponseSuccess<>("SUCCESS", result);
    }

}
