package com.dodal.meet.controller.response.user;

import com.dodal.meet.controller.response.category.TagResponse;
import com.dodal.meet.controller.response.category.UserCategoryResponse;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.UserRole;
import com.dodal.meet.model.entity.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Getter
@Schema(description = "유저 정보 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInfoResponse {


    @Schema(description = "유저 시퀀스 번호", example = "1")
    private Long userId;

    @Schema(description = "유저 소셜 아이디", example = "2843361325")
    private String socialId;

    @Schema(description = "유저 소셜 타입", example = "KAKAO")
    private SocialType socialType;

    @Schema(description = "유저 권한", example = "USER")
    private UserRole role;
    @Schema(description = "유저 이메일", example = "sasca37@naver.com")
    private String email;
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

    @Schema(description = "알람 허용 여부", example = "Y", allowableValues = {"Y", "N"})
    private char alarmYn;

    @Schema(description = "신고 누적 횟수", example = "0")
    private int accuseCnt;

    @Schema(description = "FCM 토큰 - 접속 시 마다 갱신", example = "FCM 토큰")
    private String fcmToken;

    @Schema(description = "자체 생성 리프레시 토큰 - 30일 뒤 만료", example = "서버 리프레시 토큰")
    private String refreshToken;

    @Schema(description = "가입시간", example = "2023-06-25T12:06:23.573+00:00")
    private Timestamp registerAt;

    public static UserInfoResponse newInstance(UserEntity userEntity, List<UserTagEntity> userTagEntityList, List<CategoryEntity> categoryList) {
        return UserInfoResponse.builder()
                .userId(userEntity.getId())
                .socialId(userEntity.getSocialId())
                .socialType(userEntity.getSocialType())
                .role(userEntity.getRole())
                .email(userEntity.getEmail())
                .nickname(userEntity.getNickname())
                .profileUrl(userEntity.getProfileUrl())
                .content(userEntity.getContent())
                .alarmYn(userEntity.getAlarmYn())
                .accuseCnt(userEntity.getAccuseCnt())
                .categoryList(UserCategoryResponse.listFrom(categoryList))
                .tagList(TagResponse.userEntitesToList(userTagEntityList))
                .fcmToken(userEntity.getTokenEntity().getFcmToken())
                .refreshToken(userEntity.getTokenEntity().getRefreshToken())
                .registerAt(userEntity.getRegisteredAt())
                .build();
    }
}
