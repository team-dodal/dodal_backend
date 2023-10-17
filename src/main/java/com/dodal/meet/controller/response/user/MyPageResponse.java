package com.dodal.meet.controller.response.user;

import com.dodal.meet.controller.response.category.TagResponse;
import com.dodal.meet.controller.response.category.UserCategoryResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Builder
@Getter
@Schema(description = "마이페이지 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyPageResponse {
    @Schema(description = "유저 시퀀스 번호", example = "1")
    private Long userId;

    @Schema(description = "유저 닉네임", example = "노래하는 어피치")
    private String nickname;

    @Schema(description = "유저 프로필 url", example = "https://dodal-bucket.s3.com/skdmks.png")
    private String profileUrl;

    @Schema(description = "유저 한 줄 소개", example = "안녕하세요")
    private String content;

    @Schema(description = "관심 있는 카테고리 정보")
    private List<UserCategoryResponse> categoryList;

    @Schema(description = "관심 있는 태그 정보")
    private List<TagResponse> tagList;

    private List<ChallengeRoomResponse> challengeRoomList;

    @Schema(description = "최대 연속 인증 횟수", example = "10")
    private int maxContinueCertCnt;

    @Schema(description = "현재 연속 인증 횟수", example = "50")
    private int totalCertCnt;

}
