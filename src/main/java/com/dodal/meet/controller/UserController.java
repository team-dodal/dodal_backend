package com.dodal.meet.controller;

import com.dodal.meet.controller.request.user.*;
import com.dodal.meet.controller.response.ResponseFail;
import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.controller.response.user.*;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.List;

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
    public ResponseEntity<ResponseSuccess<UserSignInResponse>> signIn(@Valid @RequestBody UserSignInRequest request) {
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
    @PostMapping(value = "/sign-up", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseSuccess<UserSignUpResponse>> signUp(
                                                                            @Schema(name =  "social_type", example = "KAKAO")
                                                                            @RequestParam(name = "social_type") SocialType socialType,

                                                                            @NotBlank(message = "social_id는 필수값입니다.")
                                                                            @Schema(name =  "social_id", example = "2843361325")
                                                                            @RequestParam(name = "social_id") String socialId,

                                                                            @Email(message = "이메일 형식에 맞지 않습니다.") @Schema(name =  "email", example = "sasca37@naver.com")
                                                                            @RequestParam(name = "email", required = false) String email,

                                                                            @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]{1,16}$", message = "nickname은 한글, 영어, 숫자로만 이루어진 1자리 이상 16자리 이하의 값이어야 합니다.")
                                                                            @Schema(name =  "nickname", example = "노래하는 어피치")
                                                                            @RequestParam(name = "nickname") String nickname,

                                                                            @Pattern(regexp = "^(.{0}|.{1,40})$", message = "값은 40자리 이하이어야 합니다.")
                                                                            @Schema(name =  "content", example = "안녕하세요")
                                                                            @RequestParam(name = "content", required = false) String content,

                                                                            @ArraySchema(schema = @Schema(implementation = String.class, name =  "tag_list", type = "array", example = "[\"001001\", \"002001\", \"003001\"]"))
                                                                            @RequestParam(name = "tag_list") List<String> tagList,

                                                                            @Schema(name = "profile")
                                                                            @Parameter(name = "profile", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                                            @RequestParam(name = "profile", required = false) MultipartFile profile
                                                                            ) {
        final String trimNickname = nickname.trim();
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
                .socialType(socialType)
                .socialId(socialId)
                .email(email)
                .nickname(trimNickname)
                .content(content)
                .tagList(tagList)
                .build();
        return ResponseEntity.created(URI.create("/sign-up")).body(ResponseSuccess.success(userService.signUp(userSignUpRequest, profile)));
    }

    @Operation(summary = "유저 정보 수정 API"
            , description = "사용자 정보를 수정한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "400", description = "실패 - INVALID_REQUEST_FIELD, ILLEGAL_IMAGE_REQUEST, INVALID_IMAGE_REQUEST, INVALID_TAG_LIST_FIELD", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseSuccess<UserInfoResponse>> updateUser(
                                                                @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]{1,16}$", message = "nickname은 한글, 영어, 숫자로만 이루어진 1자리 이상 16자리 이하의 값이어야 합니다.")
                                                                @Schema(name =  "nickname", example = "노래하는 어피치")
                                                                @RequestParam(name = "nickname", required = false) String nickname,

                                                                @Pattern(regexp = "^(.{0}|.{1,40})$", message = "값은 40자리 이하이어야 합니다.")
                                                                @Schema(name =  "content", example = "안녕하세요")
                                                                @RequestParam(name = "content", required = false) String content,

                                                                @ArraySchema(schema = @Schema(implementation = String.class, name =  "tag_list", type = "array", example = "[\"001001\", \"002001\", \"003001\"]"))
                                                                @RequestParam(name = "tag_list", required = false) List<String> tagList,

                                                                @Schema(name = "profile") @Parameter(name = "profile", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                                @RequestParam(name = "profile", required = false) MultipartFile profile,

                                                                @Schema(name = "profile_url", example = "https://s3.console.aws.amazon.com/s3/object/dodal-bucket?region=ap-northeast-2&prefix=15e4f096-2a33-4634-8ba1-88b351ee0a95..ico")
                                                                @RequestParam(name = "profile_url", required = false) String profileUrl,

                                                                Authentication authentication
                                                                ) {
        final String trimNickname = nickname.trim();
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .nickname(trimNickname)
                .content(content)
                .tagList(tagList)
                .build();
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.updateUser(userUpdateRequest, profile, profileUrl, authentication)));
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
        final String trimNickname = nickname.trim();
        final boolean isExistNickname = userService.findByNickname(trimNickname);
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
    public ResponseEntity<ResponseSuccess<UserInfoResponse>> getUser(Authentication authentication) {
        return ResponseEntity.ok().body(ResponseSuccess.success(userService.getUser(authentication)));
    }

    @Operation(summary = "FCM 토큰 저장 API"
            , description = "저장 성공인 경우 result_code : success 실패일 경우 에러 공통코드를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @PostMapping("/fcm-token")
    public ResponseEntity<ResponseSuccess<Void>> postFcmToken(@Valid @RequestBody UserFcmTokenRequest request, Authentication authentication) {
        userService.postFcmToken(request.getFcmToken(), authentication);
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
    public ResponseEntity<ResponseSuccess<UserAccessTokenResponse>> postAccessToken(Authentication authentication) {
        return ResponseEntity.created(URI.create("/access-token")).body(ResponseSuccess.success(userService.postAccessToken(authentication)));
    }

    @Operation(summary = "유저 회원 탈퇴 API"
            , description = "탈퇴 성공시 result_code SUCCESS를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/me")
    public ResponseEntity<ResponseSuccess<Void>> deleteUser(Authentication authentication) {
        userService.deleteUser(authentication);
        return ResponseEntity.noContent().build();
    }

    /*
    @Operation(summary = "월별 API"
            , description = "마이페이지 정보를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "실패 - INVALID_TOKEN", content = @Content(schema = @Schema(implementation = ResponseFail.class))),
                    @ApiResponse(responseCode = "500", description = "실패 - INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ResponseFail.class)))
            })
    @DeleteMapping("/me/challenge-room")
    public ResponseEntity<ResponseSuccess<Void>> deleteUser(Authentication authentication) {
        userService.deleteUser(authentication);
        return ResponseEntity.noContent().build();
    }
     */
}
