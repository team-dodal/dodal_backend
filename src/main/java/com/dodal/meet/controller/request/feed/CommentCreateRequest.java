package com.dodal.meet.controller.request.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@Schema(description = "피드 댓글 요청")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CommentCreateRequest {

    @Schema(description = "댓글 부모 ID (null 또는 숫자)", example = "1")
    private Long parentId;

    @Pattern(regexp = "^(.{0}|.{1,100})$", message = "값은 100자리 이하이어야 합니다.")
    @Schema(description = "내용", example = "테스트 내용 입니다.")
    private String content;

}
