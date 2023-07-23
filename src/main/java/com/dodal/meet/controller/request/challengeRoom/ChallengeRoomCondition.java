package com.dodal.meet.controller.request.challengeRoom;


import com.dodal.meet.model.RoomSearchType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@Schema(description = "도전방 검색 조건 요청")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChallengeRoomCondition {
    @Schema(description = "검색 조건", example = "RECENCY")
    private RoomSearchType roomSearchType;

    @Schema(description = "태그 값", example = "001002")
    private String tagValue;
}
