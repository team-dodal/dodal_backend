package com.dodal.meet.controller.response;


import com.dodal.meet.model.entity.CommonCodeEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "공통 코드 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class CommonCodeResponse {

    @Schema(description = "카테고리", example = "ACCUSE")
    private String category;

    @Schema(description = "카테고리명", example = "신고 코드")
    private String categoryName;

    List<CommonCodeInfo> codeInfoList;

    public static CommonCodeResponse commonCodeEntityToDto(List<CommonCodeEntity> entityList) {
        CommonCodeEntity findEntity = entityList.get(0);
        CommonCodeResponse dto = CommonCodeResponse.builder().category(findEntity.getCategory()).categoryName(findEntity.getCategoryName()).codeInfoList(new ArrayList<>()).build();
        entityList.forEach(entity -> dto.codeInfoList.add(CommonCodeInfo.builder().code(entity.getCode()).codeName(entity.getCodeName()).build()));
        return dto;
    }
}
