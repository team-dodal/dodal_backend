package com.dodal.meet.controller.response.category;

import com.dodal.meet.model.entity.TagEntity;
import com.dodal.meet.model.entity.UserTagEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class TagResponse {

    @Schema(description = "태그명", example = "체중 관리")
    private String name;

    @Schema(description = "태그값", example = "001001")
    private String value;

    public static List<TagResponse> tagEntitiesToList(List<TagEntity> tagEntities) {
        return tagEntities.stream().map(entity -> fromTagEntity(entity)).collect(Collectors.toList());
    }

    public static List<TagResponse> userEntitesToList(List<UserTagEntity> userTagEntities) {
        return userTagEntities.stream().map(entity -> fromUserEntity(entity)).collect(Collectors.toList());
    }

    private static TagResponse fromTagEntity(TagEntity entity) {
        return TagResponse.builder()
                .name(entity.getName())
                .value(entity.getValue())
                .build();
    }

    private static TagResponse fromUserEntity(UserTagEntity entity) {
        return TagResponse.builder()
                .name(entity.getTagName())
                .value(entity.getTagValue())
                .build();
    }
}
