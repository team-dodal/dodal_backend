package com.dodal.meet.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@Schema(description = "실패 응답 처리")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class ResponseFail<T> extends Response{

    @Schema(description = "응답 코드", example = "FAIL_CODE")
    private String resultCode;

    @Schema(description = "응답 메시지", example = "FAIL_MESSAGE")
    private T result;

    public ResponseFail(String resultCode, T result) {
        super(resultCode, result);
    }
}
