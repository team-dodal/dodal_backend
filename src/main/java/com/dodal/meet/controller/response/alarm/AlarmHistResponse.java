package com.dodal.meet.controller.response.alarm;

import com.dodal.meet.model.entity.AlarmHistEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Builder
@Schema(description = "알림 이력 응답")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AlarmHistResponse {

    @Schema(description = "유저 아이디 시퀀스", example = "10")
    private Long userId;

    @Schema(description = "도전방 시퀀스", example = "10")
    private int roomId;

    @Schema(description = "제목", example = "매일매일 자격증 공부!")
    private String title;

    @Schema(description = "내용", example = "도전방 인증 요청이 왔습니다.")
    private String content;

    @Schema(description = "알림 일자", example = "20231010")
    private String registeredDate;

    public static AlarmHistResponse entityToAlarmHistResponse(AlarmHistEntity alarmHistEntity) {
        return AlarmHistResponse
                .builder()
                .userId(alarmHistEntity.getUserId())
                .roomId(alarmHistEntity.getRoomId())
                .title(alarmHistEntity.getTitle())
                .content(alarmHistEntity.getContent())
                .registeredDate(alarmHistEntity.getRegisteredDate())
                .build();
    }

}