package com.dodal.meet.controller;

import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.category.CategoryAndTagInfoResponse;
import com.dodal.meet.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Category", description = "카테고리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리와 하위 태그 전체 조회 API"
            , description = "카테고리 정보와 카테고리에 해당하는 태그 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "500", description = "실패 - NOT_FOUND_TAG", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/categories/tags")
    public ResponseEntity<EntityModel<Response<CategoryAndTagInfoResponse>>> getCategoryAndTags() {
        Link selfRel = linkTo(methodOn(CategoryController.class).getCategoryAndTags()).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(categoryService.getCategoryAndTags()), selfRel), HttpStatus.OK);
    }

}
