package com.dodal.meet.controller.request.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@Schema(description = "FCM 푸쉬 요청")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class FcmPushRequest {

    @NotBlank(message = "title 값은 필수 값입니다.")
    @Schema(description = "제목", example = "테스트 제목 입니다.")
    private String title;

    @NotBlank(message = "body 값은 필수 값입니다.")
    @Schema(description = "내용", example = "테스트 내용 입니다.")
    private String body;

}
