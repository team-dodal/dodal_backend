package com.dodal.meet.service;


import com.dodal.meet.controller.request.user.UserLoginRequest;
import com.dodal.meet.controller.response.auth.GoogleLoginResponse;
import com.dodal.meet.controller.response.auth.KaKaoLoginResponse;
import com.dodal.meet.controller.response.user.UserLoginResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialService {
    private final String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
    private final String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    private final UserEntityRepository userEntityRepository;

    private final RestTemplate restTemplate;

    @Value("${jwt.secret-key}")
    private String jwtKey;

    @Transactional
    public UserLoginResponse kakaoLogin(UserLoginRequest request) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        header.setBearerAuth(request.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(header);

        try {
            ResponseEntity<KaKaoLoginResponse> result = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, entity, KaKaoLoginResponse.class);

            final String kakaoEmail = result.getBody().getKaKaoAccount().getEmail();
            final String accessToken = JwtTokenUtils.generateAccessToken(kakaoEmail, jwtKey);
            final String refreshToken = JwtTokenUtils.generateRefreshToken(kakaoEmail, jwtKey);

            UserEntity user = userEntityRepository.findByEmailAndSocialType(kakaoEmail, SocialType.KAKAO).orElse(null);
            if (user == null) {
                user = UserEntity.of(kakaoEmail, request.getSocialType(), refreshToken);
            } else {
                user.setRefreshToken(refreshToken);
            }
            userEntityRepository.save(user);

            return UserLoginResponse.builder()
                    .socialType(request.getSocialType())
                    .email(kakaoEmail)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new DodalApplicationException(ErrorCode.INVALID_TOKEN);
            }
        }
        throw new DodalApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public UserLoginResponse googleLogin(UserLoginRequest request) {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + request.getAccessToken());
        header.setBearerAuth(request.getAccessToken());
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(header);
        try {
            ResponseEntity<GoogleLoginResponse> userInfo = restTemplate.exchange(
                    GOOGLE_USERINFO_REQUEST_URL + "?access_token=" + request.getAccessToken(),
                    HttpMethod.GET, entity, GoogleLoginResponse.class);

            log.info("GOOGLE USERINFO : "  + userInfo);

            final String googleEmail = userInfo.getBody().getEmail();
            final String accessToken = JwtTokenUtils.generateAccessToken(googleEmail, jwtKey);
            final String refreshToken = JwtTokenUtils.generateRefreshToken(googleEmail, jwtKey);

            UserEntity user = userEntityRepository.findByEmailAndSocialType(googleEmail, SocialType.GOOGLE).orElse(null);
            if (user == null) {
                user = UserEntity.of(googleEmail, request.getSocialType(), refreshToken);
            } else {
                user.setRefreshToken(refreshToken);
            }
            userEntityRepository.save(user);

            return UserLoginResponse.builder()
                    .socialType(request.getSocialType())
                    .email(googleEmail)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new DodalApplicationException(ErrorCode.INVALID_TOKEN);
            }
        }
        throw new DodalApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
