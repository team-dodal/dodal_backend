package com.dodal.meet.model.entity;

import com.dodal.meet.controller.request.challengeroom.ChallengeFeedCreateRequest;
import com.dodal.meet.model.BaseTime;
import com.dodal.meet.utils.DateUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenge_feed")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeFeedEntity extends BaseTime {

    @Id
    @Column(name = "challenge_feed_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String certImgUrl;

    @Column(nullable = true, length = 100)
    private String certContent;

    @Column(nullable = false, length = 16)
    private int likeCnt;

    @Column(nullable = false, length = 100)
    private int commentCnt;

    @Column(nullable = false, length = 16)
    private String certCode;

    @Column(nullable = false, length = 16)
    private int accuseCnt;

    @Column(nullable = false, length = 8)
    private String registeredDate;

    @Column(nullable = false)
    private Integer roomId;

    @Column(nullable = false, length = 255)
    private String roomTitle;

    @Column(nullable = false, length = 50)
    private String challengeTagId;

    @Column(nullable = false, length = 16)
    private int continueCertCnt;

    @Builder.Default
    @OneToMany(mappedBy = "challengeFeedEntity", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FeedLikeEntity> feedLikeEntityList = new ArrayList<>();
    @PrePersist
    void prePersist() {
        this.certCode="1";
        this.likeCnt = 0;
        this.accuseCnt = 0;
        this.commentCnt = 0;
        this.registeredDate = DateUtils.getToday();
    }

    public void updateCertCode(String code) {
        this.certCode = code;
    }

    public void updateContinueCertCnt(int continueCertCnt) {
        this.continueCertCnt = continueCertCnt;
    }

    public void updateLikeCntByNum(int num) {
        this.likeCnt += num;
    }

    public void updateCommentCntByNum(int num) {
        this.commentCnt += num;
    }

    public static ChallengeFeedEntity newInstance(ChallengeFeedCreateRequest request, ChallengeRoomEntity challengeRoom, ChallengeUserEntity challengeUser) {
        return ChallengeFeedEntity.builder()
                .userId(challengeUser.getUserEntity().getId())
                .certImgUrl(request.getCertificationImgUrl())
                .certContent(request.getContent())
                .roomId(challengeRoom.getId())
                .roomTitle(challengeRoom.getTitle())
                .challengeTagId(challengeRoom.getChallengeTagEntity().getTagValue())
                .build();
    }
}
