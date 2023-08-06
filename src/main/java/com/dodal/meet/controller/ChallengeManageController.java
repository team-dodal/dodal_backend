package com.dodal.meet.controller;


import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.challengemanage.ChallengeCertImgManage;
import com.dodal.meet.controller.response.challengemanage.ChallengeHostRoleResponse;
import com.dodal.meet.controller.response.challengemanage.ChallengeUserRoleResponse;
import com.dodal.meet.service.ChallengeListService;
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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "ChallengeManage", description = "도전방 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/challenges")
@Slf4j
@Validated
public class ChallengeManageController {

    private final ChallengeListService challengeListService;

    @Operation(summary = "진행 중인 도전 API"
            , description = "유저가 사용자로 참여중인 도전방 정보를 반환한다. (미인증 / 거절 / 요청 중 / 승인 순서로 정렬)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/user")
    public ResponseEntity<EntityModel<Response<List<ChallengeUserRoleResponse>>>> getUserRoleChallengeRooms(Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeManageController.class).getUserRoleChallengeRooms(authentication)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeListService.getUserRoleChallengeRooms(authentication)), selfRel), HttpStatus.OK);
    }

    @Operation(summary = "운영 중인 도전 API"
            , description = "유저가 방장으로 관리중인 도전방 정보를 반환한다. (인증 요청 중인 개수 기준으로 정렬)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/host")
    public ResponseEntity<EntityModel<Response<List<ChallengeHostRoleResponse>>>> getHostRoleChallengeRooms(Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeManageController.class).getHostRoleChallengeRooms(authentication)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeListService.getHostRoleChallengeRooms(authentication)), selfRel), HttpStatus.OK);
    }

    @Operation(summary = "도전방 인증 관리 API"
            , description = "유저가 방장으로 관리중인 도전방에서 요청 온 인증 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @GetMapping("/manage/{room_id}/certifications")
    public ResponseEntity<EntityModel<Response<Map<String, List<ChallengeCertImgManage>>>>> getCertImgList(@PathVariable(name = "room_id") Integer roomId, @RequestParam(name = "date_ym") String dateYM, Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeManageController.class).getCertImgList(roomId, dateYM, authentication)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(challengeListService.getCertImgList(roomId, dateYM, authentication)), selfRel), HttpStatus.OK);
    }

    @Operation(summary = "도전방 피드 승인 / 거절 API"
            , description = "유저가 요청한 피드를 승인 하거나 거절한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND_ROOM", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN, UNAUTHORIZED_ROOM_HOST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PatchMapping("/manage/{room_id}/certifications/{feed_id}")
    public ResponseEntity<EntityModel<Response<Void>>> updateFeedStatus(@PathVariable(name = "room_id") Integer roomId, @PathVariable(name = "feed_id") Long feedId, @RequestParam(name = "confirm_yn") String confirmYN, Authentication authentication) {
        Link selfRel = linkTo(methodOn(ChallengeManageController.class).updateFeedStatus(roomId, feedId, confirmYN, authentication)).withSelfRel();
        challengeListService.updateFeedStatus(roomId, feedId, confirmYN, authentication);
        return new ResponseEntity<>(EntityModel.of(Response.success(), selfRel), HttpStatus.OK);
    }
}
