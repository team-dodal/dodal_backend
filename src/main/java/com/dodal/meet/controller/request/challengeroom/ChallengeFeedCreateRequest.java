package com.dodal.meet.controller.request.challengeroom;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Schema(description = "도전방 공지사항 등록 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChallengeFeedCreateRequest {

    @NotBlank(message = "certification_img_url은 필수값입니다.")
    @Schema(description = "인증 이미지 url", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String certificationImgUrl;

    @Size(min = 1, max = 100, message = "content는 1자 ~ 100자 사이여야 합니다.")
    @Schema(description =  "인증설명 글", example = "인증합니다.")
    private String content;
}
