package com.dodal.meet.controller.response.category;

import com.dodal.meet.model.entity.CategoryEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CategoryResponse {

    @Schema(description = "카테고리명", example = "건강")
    private String name;

    @Schema(description = "카테고리 서브명", example = "불끈불끈")
    private String subName;

    @Schema(description = "카테고리값", example = "001")
    private String value;

    @Schema(description = "이모지", example = "💪")
    private String emoji;

    @Schema(description = "태그정보", example = "체중 관리, 홈 트레이닝 등")
    private List<TagResponse> tags;

    public static CategoryResponse newInstance(CategoryEntity entity) {
        return CategoryResponse.builder()
                .name(entity.getName())
                .subName(entity.getSubName())
                .value(entity.getCategoryValue())
                .tags(TagResponse.tagEntitiesToList(entity.getTagEntities()))
                .emoji(entity.getEmoji())
                .build();
    }
}
