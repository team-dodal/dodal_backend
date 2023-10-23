package com.dodal.meet.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "공통 코드 정보")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class CommonCodeInfo {

    @Schema(description = "코드", example = "001")
    private String code;

    @Schema(description = "코드명", example = "상업적/홍보성")
    private String codeName;
}
