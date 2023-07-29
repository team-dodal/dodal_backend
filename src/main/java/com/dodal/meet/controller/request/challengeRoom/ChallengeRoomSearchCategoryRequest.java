package com.dodal.meet.controller.request.challengeRoom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Schema(description = "도전방 생성 요청")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChallengeRoomSearchCategoryRequest {
    private String categoryValue;

    private String tagValue;

    private String conditionCode;

    private List<Integer> certCntList;

    private Integer page;

    private Integer pageSize;

}
