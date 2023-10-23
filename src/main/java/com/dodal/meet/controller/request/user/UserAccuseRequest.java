package com.dodal.meet.controller.request.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Schema(description = "유저 신고하기 요청")
@ToString
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserAccuseRequest {
    @NotBlank(message = "fcm_token은 필수 값입니다.")
    @Schema(description = "신고 코드", example = "001")
    private String accuseCode;

    @Size(max = 500, message = "content는 500자 이내여야 합니다.")
    @Schema(description =  "기타 - 신고 내용 (500자 이내)", example = "신고합니다.")
    private String content;
}
