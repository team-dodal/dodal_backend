package com.dodal.meet.model.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "challenge_bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeBookmarkEntity {

    @Id
    @Column(name = "challenge_bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_room_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeRoomEntity challengeRoomEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity userEntity;

    private Timestamp registeredAt;

    @PrePersist
    void prePersist() {
        this.registeredAt = Timestamp.from(Instant.now());
    }
}
