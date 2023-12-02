package com.dodal.meet.controller.response.category;

import com.dodal.meet.model.entity.TagEntity;
import com.dodal.meet.model.entity.UserTagEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@ToString
public class TagResponse {

    @Schema(description = "태그명", example = "체중 관리")
    private String name;

    @Schema(description = "태그값", example = "001001")
    private String value;

    public static List<TagResponse> tagEntitiesToList(List<TagEntity> tagEntities) {
        return tagEntities.stream().map(entity -> newInstance(entity)).collect(Collectors.toList());
    }

    public static List<TagResponse> userEntitesToList(List<UserTagEntity> userTagEntities) {
        return userTagEntities.stream().map(entity -> newInstance(entity)).collect(Collectors.toList());
    }

    private static TagResponse newInstance(TagEntity entity) {
        return TagResponse.builder()
                .name(entity.getName())
                .value(entity.getTagValue())
                .build();
    }

    private static TagResponse newInstance(UserTagEntity entity) {
        return TagResponse.builder()
                .name(entity.getTagName())
                .value(entity.getTagValue())
                .build();
    }
}
