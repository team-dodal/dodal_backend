package com.dodal.meet.model.entity;

import com.dodal.meet.utils.DateUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenge_feed")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeFeedEntity {

    @Id
    @Column(name = "challenge_feed_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String certImgUrl;

    private String certContent;

    private int likeCnt;

    private int commentCnt;

    private String certCode;

    private int accuseCnt;

    private String registeredDate;
    private Timestamp registeredAt;

    private Integer roomId;

    private String roomTitle;

    private String challengeTagId;

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
        this.registeredAt = Timestamp.from(Instant.now());
        this.registeredDate = DateUtils.parsingTimestamp(this.registeredAt);
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
}
