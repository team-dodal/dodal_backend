package com.dodal.meet.controller.response.category;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@Setter
@Schema(description = "카테고리와 태그 정보 응답")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class CategoryAndTagInfoResponse {

    @Schema(description = "카테고리 / 하위 태그 정보")
    private List<CategoryResponse> categories;

}
