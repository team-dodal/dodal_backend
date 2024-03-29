package com.dodal.meet.controller.response;

import com.dodal.meet.model.entity.CommonCodeEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(description = "공통 코드 정보")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@ToString
public class CommonCodeInfo {

    @Schema(description = "코드", example = "001")
    private String code;

    @Schema(description = "코드명", example = "상업적/홍보성")
    private String codeName;

    public static CommonCodeInfo newInstance(final CommonCodeEntity entity) {
        return CommonCodeInfo.builder().code(entity.getCode()).codeName(entity.getCodeName()).build();
    }
}
