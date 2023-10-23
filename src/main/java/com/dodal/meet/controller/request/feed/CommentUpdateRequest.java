package com.dodal.meet.controller.request.feed;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@Schema(description = "피드 댓글 수정")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentUpdateRequest {

    @Schema(description = "댓글 시퀀스", example = "1")
    private Long commentId;

    @Pattern(regexp = "^(.{0}|.{1,100})$", message = "값은 100자리 이하이어야 합니다.")
    @Schema(description = "수정할 내용", example = "테스트 내용 입니다.")
    private String content;

}
