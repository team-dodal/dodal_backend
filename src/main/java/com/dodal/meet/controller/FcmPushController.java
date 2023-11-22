package com.dodal.meet.controller;


import com.dodal.meet.controller.request.fcm.FcmKafkaPush;
import com.dodal.meet.controller.request.fcm.FcmPushRequest;
import com.dodal.meet.controller.response.ResponseFail;
import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.producer.PushProducer;
import com.dodal.meet.service.FcmPushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Tag(name = "Fcm", description = "FCM PUSH 알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
@Slf4j
public class FcmPushController {

    private final PushProducer pushProducer;


    @Operation(summary = "FCM 일대일 PUSH API"
            , description = "요청한 사용자에게 FCM PUSH 메시지를 보낸다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FILED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/{receive_user_id}")
    public ResponseEntity<ResponseSuccess<Void>> sendFcmPushUser(@PathVariable(name = "receive_user_id") Long receiveUserId, @Valid @RequestBody final FcmPushRequest fcmPushRequest) {
        pushProducer.send(FcmKafkaPush.makeKafkaPush(receiveUserId, fcmPushRequest));
        return ResponseEntity.ok().body(ResponseSuccess.success());
    }
}
