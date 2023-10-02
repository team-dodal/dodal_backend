package com.dodal.meet.controller;


import com.dodal.meet.controller.response.ResponseFail;
import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.controller.response.alarm.AlarmHistResponse;
import com.dodal.meet.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Alarm", description = "알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarm")
@Slf4j
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "알림 이력 조회 API"
            , description = "사용자에게 알림온 이력을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FILED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/{user_id}")
    public ResponseEntity<ResponseSuccess<List<AlarmHistResponse>>> getAlarmHists(@PathVariable(name = "user_id") Long userId) {
        return ResponseEntity.ok().body(ResponseSuccess.success(alarmService.getAlarmHists(userId)));
    }

    @Operation(summary = "알림 이력 전체 삭제 API"
            , description = "알림 이력을 모두 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FILED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/{user_id}")
    public ResponseEntity<ResponseSuccess<Void>> delAlarmHists(@PathVariable(name = "user_id") Long userId) {
        alarmService.delAlarmHists(userId);
        return ResponseEntity.noContent().build();
    }
}
