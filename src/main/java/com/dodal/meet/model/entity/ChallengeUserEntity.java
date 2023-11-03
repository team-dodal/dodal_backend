package com.dodal.meet.model.entity;


import com.dodal.meet.model.RoomRole;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "challenge_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeUserEntity {

    @Id
    @Column(name = "challenge_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_room_id")
    private ChallengeRoomEntity challengeRoomEntity;

    @Enumerated(EnumType.STRING)
    private RoomRole roomRole;

    private int continueCertCnt;

    private int maxContinueCertCnt;

    private int totalCertCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity userEntity;

    private Timestamp registeredAt;

    @PrePersist
    void prePersist() {
        this.continueCertCnt = 0;
        this.maxContinueCertCnt = 0;
        this.totalCertCnt = 0;
        this.registeredAt = Timestamp.from(Instant.now());
    }

    // 연관 관계 편의 메서드
    public void addChallengeRoomEntity(ChallengeRoomEntity challengeRoomEntity) {
        if (this.challengeRoomEntity != null) {
            this.challengeRoomEntity.getChallengeUserEntities().remove(this);
        }
        this.challengeRoomEntity = challengeRoomEntity;
        challengeRoomEntity.getChallengeUserEntities().add(this);
    }

    public static ChallengeUserEntity getHostEntity(UserEntity userEntity) {
        return ChallengeUserEntity.builder()
                .roomRole(RoomRole.HOST)
                .userEntity(userEntity)
                .challengeRoomEntity(null)
                .build();
    }

    public void updateCertCnts(int num) {
        this.continueCertCnt += num;
        this.totalCertCnt += num;
        if (this.continueCertCnt > this.maxContinueCertCnt) {
            this.maxContinueCertCnt = this.continueCertCnt;
        }
    }

    public void updateRole(RoomRole role) {
        this.roomRole = role;
    }
}
