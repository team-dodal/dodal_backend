package com.dodal.meet.controller;

import com.dodal.meet.controller.request.user.*;
import com.dodal.meet.controller.response.CommonCodeResponse;
import com.dodal.meet.controller.response.ResponseFail;
import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.controller.response.user.*;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.service.UserService;
import com.dodal.meet.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.net.URI;

@Tag(name = "User", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그인 API"
            , description = "사용자 정보(소셜 id, 소셜 type)를 받아 가입 여부를 반환한다.",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "실패 - INVALID_LOGIN_REQUEST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
            @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
            @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<ResponseSuccess<UserSignInResponse>> signIn(@Valid @RequestBody final UserSignInRequest request) {
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.signIn(request)));
    }

    @Operation(summary = "회원가입 API"
            , description = "사용자 정보(소셜 id, 소셜 type 등)을 받아서 토큰을 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_LOGIN_REQUEST", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping(value = "/sign-up")
    public ResponseEntity<ResponseSuccess<UserSignUpResponse>> signUp(@Valid @RequestBody final UserSignUpRequest request) {
        return ResponseEntity.created(URI.create("/sign-up")).body(ResponseSuccess.success(userService.signUp(request)));
    }

    @Operation(summary = "유저 정보 수정 API"
            , description = "사용자 정보를 수정한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FIELD, ILLEGAL_IMAGE_REQUEST, INVALID_IMAGE_REQUEST, INVALID_TAG_LIST_FIELD", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PatchMapping(value = "/me")
    public ResponseEntity<ResponseSuccess<UserInfoResponse>> updateUser(@Valid @RequestBody final UserUpdateRequest request, final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.updateUser(request, user)));
    }

    @Operation(summary = "닉네임 중복 확인 API"
            , description = "닉네임이 사용 가능한 경우 200 OK, 불가능한 경우 400 오류를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS 닉네임 사용 가능", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "FAIL 닉네임 사용 불가", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<ResponseSuccess<Void>> checkNickname(@Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]{1,16}$", message = "nickname은 한글, 영어, 숫자로만 이루어진 1자리 이상 16자리 이하의 값이어야 합니다.") @PathVariable String nickname) {
        final String trimSafeNickname = StringUtils.trim(nickname);
        if (StringUtils.isEmpty(trimSafeNickname)) {
            throw new DodalApplicationException(ErrorCode.INVALID_NICKNAME_FIELD);
        }
        final boolean isExistNickname = userService.findByNickname(trimSafeNickname);
        return isExistNickname ?
                ResponseEntity.badRequest().body(ResponseSuccess.fail()) :
                ResponseEntity.ok().body(ResponseSuccess.success());
    }

    @Operation(summary = "유저 정보 확인 API"
            , description = "요청한 유저 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/me")
    public ResponseEntity<ResponseSuccess<UserInfoResponse>> getUser(final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.getUser(user)));
    }

    @Operation(summary = "FCM 토큰 저장 API"
            , description = "저장 성공인 경우 result_code : success 실패일 경우 에러 공통코드를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/fcm-token")
    public ResponseEntity<ResponseSuccess<Void>> postFcmToken(@Valid @RequestBody final UserFcmTokenRequest request, final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.postFcmToken(request.getFcmToken(), user);
        return ResponseEntity.created(URI.create("/fcm-token")).body(ResponseSuccess.success());
    }

    @Operation(summary = "액세스 토큰 재발급 API"
            , description = "DB에 저장되어 있는 현재 사용자의 리프레시 토큰 정보를 통해 액세스 토큰 정보를 반환한다. 만료 시 로그인 화면으로 이동한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/access-token")
    public ResponseEntity<ResponseSuccess<UserAccessTokenResponse>> postAccessToken(final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.created(URI.create("/access-token")).body(ResponseSuccess.success(userService.postAccessToken(user)));
    }

    @Operation(summary = "유저 회원 탈퇴 API"
            , description = "탈퇴 성공시 result_code SUCCESS를 반환한다. 운영 중인 도전이 있는 경우 ROOM_DELETE_REQUIRED 400 에러를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - ROOM_DELETE_REQUIRED", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/me")
    public ResponseEntity<ResponseSuccess<Void>> deleteUser(final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "마이페이지 조회 API"
            , description = "요청한 유저 마이페이지 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/my-page")
    public ResponseEntity<ResponseSuccess<MyPageResponse>> getMyPage(final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.getMyPage(user)));
    }

    @Operation(summary = "마이페이지 도전방 월별 인증 정보 조회 API"
            , description = "마이페이지 도전방에서 월별 인증 성공한 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/my-page/challenge-room/{room_id}")
    public ResponseEntity<ResponseSuccess<MyPageCalenderResponse>> getMyPageByChallengeRoom(@PathVariable(name = "room_id") final Integer roomId, @RequestParam(name = "date_ym") final String dateYM, final Authentication authentication) {
        DateUtils.validDateYM(dateYM);
        final User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.getMyPageCalendarInfo(roomId, dateYM, user)));
    }


    @Operation(summary = "유저 신고 공통 코드 조회 API"
            , description = "유저 신고 코드 정보를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @GetMapping("/accuse/info")
    public ResponseEntity<ResponseSuccess<CommonCodeResponse>> getAccuseCode() {
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.getAccuseCode()));
    }

    @Operation(summary = "유저 신고 API"
            , description = "유저 신고 요청한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/accuse/{user_id}")
    public ResponseEntity<ResponseSuccess<Void>> postAccuseUser(@PathVariable(name = "user_id") final Long userId, @Valid @RequestBody final UserAccuseRequest request, final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.postAccuseUser(userId, request, user);
        return ResponseEntity.created(URI.create("/accuse/"+ userId)).body(ResponseSuccess.success());
    }
}
