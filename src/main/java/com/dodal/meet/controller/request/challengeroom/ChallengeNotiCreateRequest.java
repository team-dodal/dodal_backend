package com.dodal.meet.controller.request.challengeroom;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(description = "도전방 공지사항 등록 요청")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChallengeNotiCreateRequest {

    @Length(max = 50, message = "title는 50자 이하여야합니다.")
    @Schema(description = "한 줄 소개", example = "안녕하세요")
    private String title;

    @Length(max = 2000, message = "content는 2000자 이하여야합니다.")
    @Schema(description = "한 줄 소개", example = "안녕하세요")
    private String content;
}
