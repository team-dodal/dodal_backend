package com.dodal.meet.model.entity;

import com.dodal.meet.model.BaseTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "feed_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class FeedLikeEntity extends BaseTime {

    @Id
    @Column(name = "feed_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_feed_id")
    private ChallengeFeedEntity challengeFeedEntity;

    @Column(nullable = false)
    private Long likeUserId;
}
