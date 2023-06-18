package com.dodal.meet.controller;


import com.dodal.meet.controller.request.auth.GoogleAccessTokenRequest;
import com.dodal.meet.controller.request.auth.KaKaoAccessTokenRequest;
import com.dodal.meet.controller.request.user.UserLoginRequest;
import com.dodal.meet.controller.response.auth.GoogleAccessTokenResponse;
import com.dodal.meet.controller.response.auth.GoogleLoginResponse;
import com.dodal.meet.controller.response.auth.KaKaoAccessTokenResponse;
import com.dodal.meet.controller.response.auth.KaKaoLoginResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.service.SocialService;
import com.dodal.meet.service.UserService;
import com.dodal.meet.utils.SocialUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;
import java.nio.charset.StandardCharsets;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Sample", description = "테스트 API")
@RestController
@RequestMapping("/sample")
@Slf4j
@RequiredArgsConstructor
public class SampleController {

    private final RestTemplate restTemplate;
    private final SocialUtils socialUtils;
    private final SocialService socialService;
    private final UserEntityRepository userEntityRepository;

    @Value("${jwt.admin-email}")
    private String adminEmail;

    @GetMapping("/")
    @Tag(name = "Sample", description = "서버 프로필 확인")
    @ApiOperation(value = "서버 profile 확인", notes = "EC2 서버 프로필 정보 확인 (dev, prod)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상"),
            @ApiResponse(code = 500, message = "서버오류")
    })
    public EntityModel<String> sample(){
        String profile = System.getProperty("spring.profiles.active");
        return EntityModel.of("현재 서버는 " + profile +" 모드입니다.",
                linkTo(methodOn(SampleController.class).sample()).withSelfRel());
    }

    @GetMapping("/user")
    @Tag(name = "Sample", description = "어드민 유저 정보 확인")
    @ApiOperation(value = "어드민 정보", notes = "sasca37의 카카오 유저 정보 및 토큰을 확인한다.")
    public EntityModel<UserEntity> adminToken() {
        UserEntity adminInfo = userEntityRepository.findByEmailAndSocialType(adminEmail, SocialType.KAKAO)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_LOGIN_REQUEST));
        return EntityModel.of(adminInfo,
                linkTo(methodOn(SampleController.class).adminToken()).withSelfRel());
    }

    @GetMapping("/kakao/redirect")
    @ApiIgnore
    public KaKaoLoginResponse kakaoLogin(@RequestParam String code) {
        log.info("KAKAO CODE : " + code);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", socialUtils.getKakaoClientId());
        parameters.add("redirect_uri", socialUtils.getKakaoRedirectUri());
        parameters.add("code", code);
        parameters.add("client_secret", socialUtils.getKakaoClientSecret());
        KaKaoAccessTokenResponse tokenResponse = restTemplate.postForObject(
                socialUtils.getKakaoTokenUri(), parameters, KaKaoAccessTokenResponse.class);
        final String accessToken = tokenResponse.getAccessToken();

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + accessToken);
        ResponseEntity<KaKaoLoginResponse> userInfo = restTemplate.exchange(
                socialUtils.getKakaoUserInfoUri(), HttpMethod.GET, new HttpEntity<>(header), KaKaoLoginResponse.class);
        log.info("USERINFO : "  + userInfo);

        socialService.kakaoLogin(new UserLoginRequest(SocialType.KAKAO, accessToken, ""));

        return userInfo.getBody();
    }

    @GetMapping("/google/redirect")
    @ApiIgnore
    public GoogleLoginResponse googleLogin(@RequestParam String code) {
        log.info("GOOGLE CODE : " + code);
        GoogleAccessTokenRequest request = GoogleAccessTokenRequest
                .builder()
                .grantType("authorization_code")
                .clientId(socialUtils.getGoogleClientId())
                .clientSecret(socialUtils.getGoogleClientSecret())
                .redirectUri(socialUtils.getGoogleRedirectUri())
                .code(code)
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        HttpEntity<GoogleAccessTokenRequest> entity = new HttpEntity<>(request, header);
        ResponseEntity<GoogleAccessTokenResponse> getAccessToken = restTemplate.exchange(
                socialUtils.getGoogleTokenUri(), HttpMethod.POST, entity, GoogleAccessTokenResponse.class);

        final String accessToken = getAccessToken.getBody().getAccessToken();
        header = new HttpHeaders();
        header.set("Authorization", "Bearer " + accessToken);
        ResponseEntity<GoogleLoginResponse> userInfo = restTemplate.exchange(
                socialUtils.getGoogleUserInfoUri() + "?access_token=" + accessToken, HttpMethod.GET, new HttpEntity<>(header), GoogleLoginResponse.class);
        log.info("USERINFO : "  + userInfo);

        socialService.googleLogin(new UserLoginRequest(SocialType.GOOGLE, accessToken, ""));

        return userInfo.getBody();

    }

}
