package com.dodal.meet.service;


import com.dodal.meet.controller.request.user.UserLoginRequest;
import com.dodal.meet.controller.response.user.UserLoginResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserEntityRepository userEntityRepository;
    private final SocialService socialService;

    @Value("${jwt.secret-key}")
    private String key;
    public User loadUserByEmail(String email) {
        return userEntityRepository.findByEmail(email).map(User::fromEntity)
                .orElseThrow(() -> new RuntimeException("아이디 없음"));
    }
    @Transactional
    public UserLoginResponse login(UserLoginRequest request, String provider) {
        log.info("UserLoginRequest : " + request);
        if (provider.equals(SocialType.KAKAO.name())) {
            return socialService.kakaoLogin(request);
        }
        // TODO : 구글, 애플 로그인 구현
        throw new DodalApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public UserLoginResponse refresh(UserLoginRequest request) {
        log.info("UserLoginRequest : " + request);
        UserEntity entity = userEntityRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_TOKEN));
        String accessToken = JwtTokenUtils.generateRefreshToken(entity.getEmail(), key);

        return UserLoginResponse.builder()
                .socialType(SocialType.KAKAO)
                .accessToken(accessToken)
                .email(entity.getEmail())
                .refreshToken(request.getRefreshToken())
                .build();
    }
}
