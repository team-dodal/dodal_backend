package com.dodal.meet.service;

import com.dodal.meet.controller.response.category.CategoryAndTagInfoResponse;
import com.dodal.meet.controller.response.category.CategoryResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.entity.CategoryEntity;
import com.dodal.meet.repository.CategoryEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryEntityRepository categoryEntityRepository;

    @Transactional(readOnly = true)
    public CategoryAndTagInfoResponse getCategoryAndTags() {
        List<CategoryEntity> categoryEntities = categoryEntityRepository.findAllByOrderByIdAsc();
        if (categoryEntities == null) {
            throw new DodalApplicationException(ErrorCode.NOT_FOUND_TAG);
        }
        List<CategoryResponse> categoryResponses = categoryEntities.stream()
                        .map(CategoryResponse::fromEntity).collect(Collectors.toList());

        return CategoryAndTagInfoResponse
                .builder()
                .categories(categoryResponses)
                .build();
    }
}
