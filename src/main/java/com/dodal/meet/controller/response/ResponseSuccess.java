package com.dodal.meet.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@Schema(description = "성공 응답 처리")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class ResponseSuccess<T> extends Response{

    @Schema(description = "응답 코드", example = "SUCCESS")
    private String resultCode;

    @Schema(description = "응답 메시지")
    private T result;

    public ResponseSuccess(String resultCode, T result) {
        this.resultCode = resultCode;
        this.result = result;
    }
}
