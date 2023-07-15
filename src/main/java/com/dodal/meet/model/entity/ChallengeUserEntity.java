package com.dodal.meet.model.entity;


import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.security.core.Authentication;

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

    private int certCnt;

    private Long userId;

    private String nickname;

    private Timestamp registeredAt;

    @PrePersist
    void prePersist() {
        this.certCnt = 0;
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
                .userId(userEntity.getId())
                .nickname(userEntity.getNickname())
                .challengeRoomEntity(null)
                .build();
    }
}
