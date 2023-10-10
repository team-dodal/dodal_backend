package com.dodal.meet.controller;


import com.dodal.meet.controller.response.ResponseFail;
import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.controller.response.alarm.AlarmHistResponse;
import com.dodal.meet.model.User;
import com.dodal.meet.service.AlarmService;
import com.dodal.meet.service.ImageService;
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

@Tag(name = "Image", description = "이미지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/img")
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "S3 Pre-signed 조회 API"
            , description = "요청한 파일명을 기반으로 S3에 업로드할 수 있는 Pre-signed URL을 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FILED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/url/{file_name}")
    public ResponseEntity<ResponseSuccess<String>> getAlarmHists(@PathVariable("file_name") final String fileName, final Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(imageService.getPresignedUrl(fileName, user)));
    }
}
