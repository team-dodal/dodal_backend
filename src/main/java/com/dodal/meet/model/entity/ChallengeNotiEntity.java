package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "challenge_noti")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeNotiEntity {

    @Id
    @Column(name = "challenge_noti_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_room_id")
    private ChallengeRoomEntity challengeRoomEntity;

    private String title;

    private String content;


    private Timestamp registeredAt;

    @PrePersist
    void prePersist() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    // 연관 관계 편의 메서드
    public void addChallengeRoomEntity(ChallengeRoomEntity challengeRoomEntity) {
        if (this.challengeRoomEntity != null) {
            this.challengeRoomEntity.getChallengeNotiEntities().remove(this);
        }
        this.challengeRoomEntity = challengeRoomEntity;
        challengeRoomEntity.getChallengeNotiEntities().add(this);
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
