package com.dodal.meet.controller.response;


import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.entity.CommonCodeEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "공통 코드 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@ToString
public class CommonCodeResponse {

    @Schema(description = "카테고리", example = "ACCUSE")
    private String category;

    @Schema(description = "카테고리명", example = "신고 코드")
    private String categoryName;

    List<CommonCodeInfo> codeInfoList;

    public static CommonCodeResponse newInstance(List<CommonCodeEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new DodalApplicationException(ErrorCode.COMMON_CODE_ERROR);
        }
        final CommonCodeEntity findEntity = entityList.get(0);
        CommonCodeResponse dto = newInstance(findEntity);
        entityList.stream()
                .map(entity -> CommonCodeInfo.newInstance(entity))
                .forEach(dto.codeInfoList::add);

        return dto;
    }

    public static CommonCodeResponse newInstance(final CommonCodeEntity commonCodeEntity) {
        return CommonCodeResponse.builder()
                .category(commonCodeEntity.getCategory())
                .categoryName(commonCodeEntity.getCategoryName())
                .codeInfoList(new ArrayList<>())
                .build();
    }
}
