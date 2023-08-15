package com.dodal.meet.controller.response.category;

import com.dodal.meet.model.entity.CategoryEntity;
import com.dodal.meet.model.entity.HashTagEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserCategoryResponse {

    @Schema(description = "카테고리명", example = "건강")
    private String name;

    @Schema(description = "카테고리 서브명", example = "불끈불끈")
    private String subName;

    @Schema(description = "카테고리값", example = "001")
    private String value;

    @Schema(description = "이모지", example = "💪")
    private String emoji;

    @Schema(description = "해시태그", example = "#체력키우기")
    private List<String> hashTags;

    public static List<UserCategoryResponse> fromEntityList(List<CategoryEntity> entity) {
        List<UserCategoryResponse> result = new ArrayList<>();
        entity.forEach(e -> result.add(
                UserCategoryResponse.builder()
                .name(e.getName())
                .subName(e.getSubName())
                .value(e.getCategoryValue())
                .emoji(e.getEmoji())
                .hashTags(HashTagEntity.convertStringList(e.getHashTagEntities()))
                .build())
        );
        result.sort(Comparator.comparing(UserCategoryResponse::getValue));
        return result;
    }
}
