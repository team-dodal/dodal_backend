package com.dodal.meet.controller.response.feed;

import com.dodal.meet.model.entity.CommentEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Schema(description = "피드 댓글 요청")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentResponse {
    @Schema(description = "댓글 시퀀스", example = "1")
    private Long commentId;

    @Schema(description = "피드 시퀀스", example = "3")
    private Long feedId;

    @Schema(description = "유저 시퀀스", example = "1")
    private Long userId;

    @Schema(description = "유저 닉네임", example = "노래하는 어피치")
    private String nickname;

    @Schema(description = "유저 프로필 이미지", example = "https://dodal-bucket.s3.ap-northeast-2.amazonaws.com")
    private String profileUrl;

    @Schema(description = "댓글 부모 ID (댓글이면 null 대댓글이면 상위 parentId 값)", example = "1")
    private Long parentId;

    @Pattern(regexp = "^(.{0}|.{1,100})$", message = "값은 100자리 이하이어야 합니다.")
    @Schema(description = "댓글 내용", example = "테스트 내용 입니다.")
    private String content;

    @Schema(description = "댓글 생성 시간", example = "2023-10-23T16:04:37.071+09:00")
    private Timestamp registeredAt;

    @Schema(implementation = CommentResponse.class, name = "children")
    private List<CommentResponse> children;

    public static CommentResponse convertCommentToDto(CommentEntity entity) {
        return CommentResponse.builder()
                .feedId(entity.getChallengeFeedEntity().getId())
                .userId(entity.getUserId())
                .profileUrl(entity.getProfileUrl())
                .nickname(entity.getNickname())
                .commentId(entity.getId())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .content(entity.getContent())
                .children(new ArrayList<>())
                .registeredAt(entity.getRegisteredAt())
                .build();
    }
}
