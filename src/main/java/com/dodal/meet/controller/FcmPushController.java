package com.dodal.meet.controller;


import com.dodal.meet.controller.request.fcm.FcmPushRequest;
import com.dodal.meet.controller.response.Response;
import com.dodal.meet.service.FcmPushService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Fcm", description = "FCM PUSH 알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
@Slf4j
public class FcmPushController {

    private final FcmPushService fcmPushService;

    @Operation(summary = "FCM PUSH 전체 공지 API"
            , description = "DB에 저장되어 있는 모든 사용자에게 전체 공지를 보낸다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FILED", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping("/all")
    public ResponseEntity<EntityModel<Response<Void>>> sendFcmPushAllUsers(@Valid @RequestBody final FcmPushRequest fcmPushRequest) {
        fcmPushService.sendFcmPushAllUsers(fcmPushRequest);
        Link selfRel = linkTo(methodOn(FcmPushController.class).sendFcmPushAllUsers(fcmPushRequest)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(), selfRel), HttpStatus.OK) ;
    }

    @Operation(summary = "FCM 일대일 PUSH API"
            , description = "요청한 사용자에게 FCM PUSH 메시지를 보낸다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FILED", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping("/{receive_user_id}")
    public ResponseEntity<EntityModel<Response<Void>>> sendFcmPushUser(@PathVariable(name = "receive_user_id") Long receiveUserId, @Valid @RequestBody final FcmPushRequest fcmPushRequest, Authentication authentication) {
        fcmPushService.sendFcmPushUser(receiveUserId, fcmPushRequest);
        Link selfRel = linkTo(methodOn(FcmPushController.class).sendFcmPushAllUsers(fcmPushRequest)).withSelfRel();
        return new ResponseEntity<>(EntityModel.of(Response.success(), selfRel), HttpStatus.OK) ;
    }
}
