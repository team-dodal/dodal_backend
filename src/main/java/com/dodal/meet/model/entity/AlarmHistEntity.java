package com.dodal.meet.model.entity;

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
@Table(name = "alarm_hist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class AlarmHistEntity {

    @Id
    @Column(name = "alarm_hist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    private int roomId;

    private String title;

    private String content;

    private String registeredDate;

    private Timestamp registeredAt;



    @PrePersist
    void prePersist() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    public static AlarmHistEntity alarmHistResponseToEntity(AlarmHistResponse alarmHistResponse) {
        return AlarmHistEntity
                .builder()
                .userId(alarmHistResponse.getUserId())
                .roomId(alarmHistResponse.getRoomId())
                .title(alarmHistResponse.getTitle())
                .content(alarmHistResponse.getContent())
                .registeredAt(alarmHistResponse.getRegisteredAt())
                .build();
    }

    public static List<AlarmHistResponse> entityToAlarmHistResponseList(List<AlarmHistEntity> alarmHists) {
        List<AlarmHistResponse> alarmHistResponseList = new ArrayList<>();
        alarmHists.forEach(entity -> alarmHistResponseList.add(AlarmHistResponse.entityToAlarmHistResponse(entity)));
        return alarmHistResponseList;
    }
}
