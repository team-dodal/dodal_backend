package com.dodal.meet.model.entity;

import com.dodal.meet.utils.DateUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

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

    private String confirmYN;

    private int accuseCnt;

    private String registeredDate;
    private Timestamp registeredAt;

    private Integer roomId;

    private String roomTitle;

    private String challengeTagId;

    @PrePersist
    void prePersist() {
        this.confirmYN="N";
        this.likeCnt = 0;
        this.accuseCnt = 0;
        this.registeredAt = Timestamp.from(Instant.now());
        this.registeredDate = DateUtils.parsingTimestamp(this.registeredAt);
    }
}
