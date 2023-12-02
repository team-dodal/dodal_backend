package com.dodal.meet.model.entity;

import com.dodal.meet.controller.response.alarm.AlarmHistResponse;
import com.dodal.meet.model.BaseTime;
import com.dodal.meet.utils.DateUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alarm_hist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class AlarmHistEntity extends BaseTime {

    @Id
    @Column(name = "alarm_hist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 16)
    private int roomId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 8)
    private String registeredDate;

    @PrePersist
    void prePersist() {
        this.registeredDate = DateUtils.getToday();
    }

    public static AlarmHistEntity newInstance(AlarmHistResponse alarmHistResponse) {
        return AlarmHistEntity
                .builder()
                .userId(alarmHistResponse.getUserId())
                .roomId(alarmHistResponse.getRoomId())
                .title(alarmHistResponse.getTitle())
                .content(alarmHistResponse.getContent())
                .build();
    }

    public static List<AlarmHistResponse> fromList(List<AlarmHistEntity> alarmHists) {
        List<AlarmHistResponse> alarmHistResponseList = new ArrayList<>();
        alarmHists.forEach(entity -> alarmHistResponseList.add(AlarmHistResponse.newInstance(entity)));
        return alarmHistResponseList;
    }
}
