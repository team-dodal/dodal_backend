package com.dodal.meet.controller;


import com.dodal.meet.controller.request.auth.GoogleAccessTokenRequest;
import com.dodal.meet.controller.request.auth.KaKaoAccessTokenRequest;
import com.dodal.meet.controller.response.auth.GoogleAccessTokenResponse;
import com.dodal.meet.controller.response.auth.KaKaoAccessTokenResponse;
import com.dodal.meet.utils.SocialUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import java.nio.charset.StandardCharsets;

@Tag(name = "Sample", description = "테스트 API")
@RestController
@RequestMapping("/sample")
@Slf4j
@RequiredArgsConstructor
public class SampleController {

    private final RestTemplate restTemplate;
    private final SocialUtils socialUtils;

    @GetMapping("/")
    @Tag(name = "Sample", description = "서버 프로필 확인")
    @ApiOperation(value = "서버 profile 확인", notes = "EC2 서버 프로필 정보 확인 (dev, prod)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상"),
            @ApiResponse(code = 500, message = "서버오류")
    })
    public String sample(){
        String profile = System.getProperty("spring.profiles.active");
        return "현재 서버는 " + profile +" 모드입니다.";
    }

    @GetMapping("/kakao/redirect")
    @ApiIgnore
    public String kakaoLogin(@RequestParam String code) {
        log.info("KAKAO CODE : " + code);
        log.info(socialUtils.toString());
        KaKaoAccessTokenRequest request = KaKaoAccessTokenRequest
                .builder()
                .grantType("authorization_code")
                .clientId(socialUtils.getKakaoClientId())
                .clientSecret(socialUtils.getKakaoClientSecret())
                .redirectUri(socialUtils.getKakaoRedirectUri())
                .code(code)
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        HttpEntity<KaKaoAccessTokenRequest> entity = new HttpEntity<>(request, header);
        ResponseEntity<KaKaoAccessTokenResponse> getAccessToken = restTemplate.exchange(
                socialUtils.getKakaoTokenUri(), HttpMethod.POST, entity, KaKaoAccessTokenResponse.class);
        log.info(entity.getBody().toString());
        final String accessToken = getAccessToken.getBody().getAccessToken();
        /*
            header = new HttpHeaders();
            header.set("Authorization", "Bearer " + accessToken);
            ResponseEntity<KaKaoLoginResponse> userInfo = restTemplate.exchange(
                    socialUtils.getGoogleUserInfoUri(), HttpMethod.GET, new HttpEntity<>(header), KaKaoLoginResponse.class);
            log.info("USERINFO : "  + userInfo);
         */
        return accessToken;
    }

    @GetMapping("/google/redirect")
    @ApiIgnore
    public String googleLogin(@RequestParam String code) {
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
        /*
            header = new HttpHeaders();
            header.set("Authorization", "Bearer " + accessToken);
            ResponseEntity<GoogleLoginResponse> userInfo = restTemplate.exchange(
                    socialUtils.getGoogleUserInfoUri() + "?access_token=" + accessToken, HttpMethod.GET, new HttpEntity<>(header), GoogleLoginResponse.class);
            log.info("USERINFO : "  + userInfo);
         */
        return accessToken;
    }

}
