package com.dodal.meet.model.entity;

import com.dodal.meet.controller.request.user.UserAccuseRequest;
import com.dodal.meet.controller.response.alarm.AlarmHistResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accuse")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class AccuseEntity {
    @Id
    @Column(name = "accuse_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sourceUserId;

    private Long targetUserId;

    private String accuseCode;

    private String content;

    private Timestamp registeredAt;

    @PrePersist
    void prePersist() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    public static AccuseEntity userAccuseRequestToEntity(UserAccuseRequest request, Long targetUserId, UserEntity userEntity) {
        return AccuseEntity.builder()
                .sourceUserId(userEntity.getId())
                .targetUserId(targetUserId)
                .accuseCode(request.getAccuseCode())
                .content(request.getContent())
                .build();
    }
}
