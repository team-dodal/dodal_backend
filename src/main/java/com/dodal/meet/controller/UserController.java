package com.dodal.meet.controller;

import com.dodal.meet.controller.request.user.UserLoginRequest;
import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.user.UserLoginResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Tag(name = "User", description = "로그인 로그인 API")
    @Operation(summary = "소셜 로그인 (KAKAO, GOOGLE, APPLE) 자체 생성 JWT 토큰 및 사용자 정보 반환"
            , description = "최초 로그인 시 소셜 타입, 소셜 AccessToken 정보로 request. 재 로그인 시 소셭 타입, 자체 발급 AccessToken 또는 Refresh 토큰 정보로 request",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = UserLoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "실패 - INVALID_LOGIN_REQUEST", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/login/{provider}")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request, @Parameter(name = "provider", description = "KAKAO, GOOGLE, APPLE") @PathVariable String provider) {
        boolean validAccessToken = StringUtils.hasLength(request.getAccessToken());
        boolean validRefreshToken = StringUtils.hasLength(request.getRefreshToken());
        if (validAccessToken) {
            return Response.success(userService.login(request, provider));
        } else if (validRefreshToken) {
            return Response.success(userService.refresh(request));
        }
        throw new DodalApplicationException(ErrorCode.INVALID_LOGIN_REQUEST, ErrorCode.INVALID_LOGIN_REQUEST.getMessage());
    }
}
