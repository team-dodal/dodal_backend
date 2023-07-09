package com.dodal.meet.controller.response.category;

import com.dodal.meet.model.entity.CategoryEntity;
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
public class CategoryResponse {

    @Schema(description = "카테고리명", example = "건강")
    private String name;

    @Schema(description = "카테고리값", example = "001")
    private String value;

    @Schema(description = "태그정보", example = "체중 관리, 홈 트레이닝 등")
    private List<TagResponse> tags;

    public static CategoryResponse fromEntity(CategoryEntity entity) {
        return CategoryResponse.builder()
                .name(entity.getName())
                .value(entity.getValue())
                .tags(TagResponse.tagEntitiesToList(entity.getTagEntities()))
                .build();
    }
}
