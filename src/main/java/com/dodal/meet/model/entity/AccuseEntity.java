package com.dodal.meet.model.entity;

import com.dodal.meet.controller.request.user.UserAccuseRequest;
import com.dodal.meet.model.BaseTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "accuse")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class AccuseEntity extends BaseTime {
    @Id
    @Column(name = "accuse_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sourceUserId;

    @Column(nullable = false)
    private Long targetUserId;

    @Column(nullable = false, length = 16)
    private String accuseCode;

    @Column(nullable = true, length = 500)
    private String content;

    public static AccuseEntity newInstance(UserAccuseRequest request, Long targetUserId, UserEntity userEntity) {
        return AccuseEntity.builder()
                .sourceUserId(userEntity.getId())
                .targetUserId(targetUserId)
                .accuseCode(request.getAccuseCode())
                .content(request.getContent())
                .build();
    }
}
