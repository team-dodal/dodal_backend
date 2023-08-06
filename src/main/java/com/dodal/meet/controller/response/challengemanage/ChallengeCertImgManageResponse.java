package com.dodal.meet.controller.response.challengemanage;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@Schema(description = "운영중인 도전방 피드 요청 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ChallengeCertImgManageResponse {

    @Schema(description = "인증 요청 일자", example = "20230801")
    private String registeredDate;

    @Schema(description = "요청 일자 별 인증 요청 리스트", example = "20230801")
    List<ChallengeCertImgManage> certImgList;
}
