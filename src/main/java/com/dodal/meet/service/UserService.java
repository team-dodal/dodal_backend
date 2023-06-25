package com.dodal.meet.service;


import com.dodal.meet.controller.request.user.UserSignInRequest;
import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.controller.response.user.UserSignInResponse;
import com.dodal.meet.controller.response.user.UserSignUpResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.TokenEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.TokenEntityRepository;
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
    private final TokenEntityRepository tokenEntityRepository;

    @Value("${jwt.secret-key}")
    private String jwtKey;




    @Transactional(readOnly = true)
    public UserSignInResponse signIn(UserSignInRequest request) {
        final String socialId = request.getSocialId();
        final SocialType socialType = request.getSocialType();
        UserEntity user = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType).orElse(null);
        if (user != null) {
            final String accessToken = JwtTokenUtils.generateAccessToken(socialId, socialType, jwtKey);
            final String refreshToken = JwtTokenUtils.generateRefreshToken(socialId, socialType, jwtKey);

            return UserSignInResponse.builder()
                    .isSigned("true")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            return UserSignInResponse.builder()
                    .isSigned("false")
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public User findBySocialIdAndSocialTypeToUser(String socialId, SocialType socialType) {
        return userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .map(User::fromEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }

    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequest request) {
        final String socialId = request.getSocialId();
        final SocialType socialType = request.getSocialType();

        // 회원가입되어 있는 지 체크
        UserEntity entity = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType).orElse(null);
        if (entity != null) {
            throw new DodalApplicationException(ErrorCode.INVALID_SIGNUP_REQUEST);
        }

        final String accessToken = JwtTokenUtils.generateAccessToken(socialId, socialType, jwtKey);
        final String refreshToken = JwtTokenUtils.generateRefreshToken(socialId, socialType, jwtKey);

        TokenEntity tokenEntity = TokenEntity.builder().fcmToken(request.getFcmToken()).refreshToken(refreshToken).build();
        tokenEntityRepository.save(tokenEntity);

        entity = UserEntity.SignUpDtoToEntity()
                .socialId(request.getSocialId())
                .socialType(request.getSocialType())
                .tokenEntity(tokenEntity)
                .build();

        userEntityRepository.save(entity);

        return UserSignUpResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
