package com.dodal.meet.controller;

import com.dodal.meet.controller.request.user.UserProfileRequest;
import com.dodal.meet.controller.request.user.UserSignInRequest;
import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.user.UserProfileResponse;
import com.dodal.meet.controller.response.user.UserSignInResponse;
import com.dodal.meet.controller.response.user.UserSignUpResponse;
import com.dodal.meet.model.User;
import com.dodal.meet.service.ImageService;
import com.dodal.meet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "User", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final ImageService imageService;

    @Operation(summary = "로그인 API"
            , description = "REQUEST : 사용자 정보(소셜 id, 소셜 type 등), RESPONSE : JWT TOKEN (AccessToken, RefreshToken)" +
            "사용자 정보가 없을 경우 회원가입 API를 요청한다.",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "실패 - INVALID_LOGIN_REQUEST", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/sign-in")
    public EntityModel<Response<UserSignInResponse>> signIn(@RequestBody UserSignInRequest request) {
        return EntityModel.of(Response.success(userService.signIn(request)),
                linkTo(methodOn(UserController.class).signIn(request)).withSelfRel());
    }

    @Operation(summary = "회원가입 API"
            , description = "REQUEST : 사용자 정보(소셜 id, 소셜 type 등), RESPONSE : JWT TOKEN (AccessToken, RefreshToken)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_LOGIN_REQUEST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping("/sign-up")
    public EntityModel<Response<UserSignUpResponse>> signUp(@RequestBody UserSignUpRequest request) {
        return EntityModel.of(Response.success(userService.signUp(request)),
                linkTo(methodOn(UserController.class).signUp(request)).withSelfRel(),
                linkTo(methodOn(UserController.class).signIn(null)).withRel("sign-in"));
    }

    @Operation(summary = "프로필 이미지 등록 API"
            , description = "REQUEST : Multipart/form-data 형식 이미지 업로드(키 값 : profile),  RESPONSE : S3 이미지 URL",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_IMAGE_REQUEST", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = Response.class)))
            })
    @PostMapping(value = "/profile")
    public EntityModel<Response<UserProfileResponse>> profile(UserProfileRequest request, Authentication authentication) {
        return EntityModel.of(Response.success(imageService.uploadImage(request, authentication)));
    }
}
