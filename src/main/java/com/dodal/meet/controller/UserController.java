package com.dodal.meet.controller;


import com.dodal.meet.controller.request.user.UserLoginRequest;
import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.user.UserLoginResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "소셜 로그인 구현 (카카오톡, 구글, 애플)", notes = "현재는 카카오톡만 구현")
    @PostMapping("/login/{provider}")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request, @PathVariable String provider) {
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
