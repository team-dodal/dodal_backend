package com.dodal.meet.model.entity;


import com.dodal.meet.model.BaseTime;
import com.dodal.meet.model.RoomRole;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.annotation.Nullable;
import javax.persistence.*;

@Entity
@Table(name = "challenge_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeUserEntity extends BaseTime {

    @Id
    @Column(name = "challenge_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_room_id")
    private ChallengeRoomEntity challengeRoomEntity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomRole roomRole;

    @Column(nullable = false, length = 50)
    private int continueCertCnt;

    @Column(nullable = false, length = 50)
    private int maxContinueCertCnt;

    @Column(nullable = false, length = 50)
    private int totalCertCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity userEntity;

    @PrePersist
    void prePersist() {
        this.continueCertCnt = 0;
        this.maxContinueCertCnt = 0;
        this.totalCertCnt = 0;
    }

    // 연관 관계 편의 메서드
    public void addChallengeRoomEntity(ChallengeRoomEntity challengeRoomEntity) {
        if (this.challengeRoomEntity != null) {
            this.challengeRoomEntity.getChallengeUserEntities().remove(this);
        }
        this.challengeRoomEntity = challengeRoomEntity;
        challengeRoomEntity.getChallengeUserEntities().add(this);
    }

    public static ChallengeUserEntity fromHostEntity(UserEntity userEntity) {
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
