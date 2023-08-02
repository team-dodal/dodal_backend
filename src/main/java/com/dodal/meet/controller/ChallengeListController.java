package com.dodal.meet.controller;


import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.challengelist.ChallengeUserRoleResponse;
import com.dodal.meet.controller.response.challengeroom.ChallengeNotiResponse;
import com.dodal.meet.service.ChallengeListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "ChallengeList", description = "진행중인 도전 리스트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/challenges")
@Slf4j
@Validated
public class ChallengeListController {

    private final ChallengeListService challengeListService;

    @Operation(summary = "진행 중인 도전 API"
            , description = "유저가 사용자로 참여중인 도전방 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/user")
    public ResponseEntity<EntityModel<Response<Page<ChallengeUserRoleResponse>>>> getUserRoleChallengeRooms(Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeListController.class).getUserRoleChallengeRooms(authentication)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeListService.getUserRoleChallengeRooms(authentication)), selfRel), HttpStatus.OK);
    }
}
