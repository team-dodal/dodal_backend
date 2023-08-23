package com.dodal.meet.model.entity;

import com.dodal.meet.utils.DateUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "feed_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class FeedLikeEntity {

    @Id
    @Column(name = "feed_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_feed_id")
    private ChallengeFeedEntity challengeFeedEntity;

    private Long likeUserId;

    private Timestamp registeredAt;

    @PrePersist
    void prePersist() {
        this.registeredAt = Timestamp.from(Instant.now());
    }
}
