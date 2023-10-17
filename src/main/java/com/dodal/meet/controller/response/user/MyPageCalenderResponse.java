package com.dodal.meet.controller.response.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Schema(description = "마이페이지 도전방 월별 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyPageCalenderResponse {

    @Schema(description = "유저 시퀀스 번호", example = "1")
    private Long userId;

    List<MyPageCalenderInfo> myPageCalenderInfoList;
}
