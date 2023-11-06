package com.dodal.meet.controller.response.challengeroom;


import com.dodal.meet.controller.response.user.UserCertPerWeek;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@Schema(description = "도전 방 생성 응답")
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChallengeCreateResponse {

    @Schema(description = "도전방 시퀀스", example = "1")
    private Integer roomId;

    @Schema(description = "썸네일 이미지 URL")
    private String thumbnailImgUrl;

    @Schema(description = "태그값", example = "001001")
    private String tagValue;

    @Schema(description = "태그명", example = "체중 관리")
    private String tagName;

    @Schema(description = "주간 인증 설정 횟수", example = "3")
    private int certCnt;

    @Schema(description = "도전방 제목", example = "매일매일 체중 관리")
    private String title;

    @Schema(description = "방장 아이디 시퀀스", example = "1")
    private Long hostId;

    @Schema(description = "방장 닉네임", example = "노래하는 어피치")
    private String hostNickname;

    @Schema(description = "방장 프로필 이미지 url", example = "https://")
    private String hostProfileUrl;

    @Schema(description = "가입 유저 수", example = "5")
    private int userCnt;

    @Schema(description = "최대 인원 수", example = "20")
    private int recruitCnt;

    @Schema(description = "도전방 소개글", example = "누구나 환영입니다.")
    private String content;

    @Schema(description = "피드 이미지 URL 리스트")
    private List<String> feedUrlList;

    @Schema(description = "인증 관련 설명 글", example = "이렇게 인증하세요.")
    private String certContent;

    @Schema(description = "올바른 인증 예시 이미지 URL")
    private String certCorrectImgUrl;

    @Schema(description = "잘못된 인증 예시 이미지 URL")
    private String certWrongImgUrl;

    @Schema(description = "도전방 북마크 수", example = "1")
    private int bookmarkCnt;

    @Schema(description = "현재 유저의 도전방 북마크 등록 여부", example = "N")
    private String bookmarkYN;

    @Schema(description = "현재 유저의 도전방 가입 여부", example = "N")
    private String joinYN;

    @Schema(description = "현재 유저의 오늘 인증 여부", example = "2")
    private String todayCertCode;

    @Schema(description = "도전방 신고 횟수", example = "0")
    private int accuseCnt;

    @Schema(description = "도전방 공지사항 제목", example = "매일 20시 인증 승인합니다.")
    private String noticeTitle;

    @Schema(description = "도전방 공지사항", example = "이렇게 인증 부탁 드립니다.")
    private String noticeContent;

    @Schema(description = "유저 주간 인증 정보")
    private List<UserCertPerWeek> userCertPerWeekList;

    @Schema(description = "도전방 생성 시간")
    private Timestamp registeredAt;

    public static ChallengeCreateResponse fromChallengeRoomDetail(ChallengeRoomDetailResponse response) {
        return ChallengeCreateResponse.builder()
                .roomId(response.getRoomId())
                .thumbnailImgUrl(response.getThumbnailImgUrl())
                .tagValue(response.getTagValue())
                .tagName(response.getTagName())
                .certCnt(response.getCertCnt())
                .title(response.getTitle())
                .hostId(response.getHostId())
                .hostNickname(response.getHostNickname())
                .hostProfileUrl(response.getHostProfileUrl())
                .userCnt(response.getUserCnt())
                .recruitCnt(response.getRecruitCnt())
                .content(response.getContent())
                .feedUrlList(response.getFeedUrlList())
                .certContent(response.getCertContent())
                .certCorrectImgUrl(response.getCertCorrectImgUrl())
                .certWrongImgUrl(response.getCertWrongImgUrl())
                .bookmarkCnt(response.getBookmarkCnt())
                .bookmarkYN(response.getBookmarkYN())
                .joinYN(response.getJoinYN())
                .todayCertCode(response.getTodayCertCode())
                .accuseCnt(response.getAccuseCnt())
                .noticeTitle(response.getNoticeTitle())
                .noticeContent(response.getNoticeContent())
                .userCertPerWeekList(response.getUserCertPerWeekList())
                .registeredAt(response.getRegisteredAt())
                .build();
    }
}
