package com.dodal.meet.service;


import com.dodal.meet.controller.request.user.UserSignInRequest;
import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.controller.response.user.UserAccessTokenResponse;
import com.dodal.meet.controller.response.user.UserInfoResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserEntityRepository userEntityRepository;
    private final TokenEntityRepository tokenEntityRepository;

    @Value("${jwt.secret-key}")
    private String jwtKey;




    @Transactional
    public UserSignInResponse signIn(UserSignInRequest request) {
        final String socialId = request.getSocialId();
        final SocialType socialType = request.getSocialType();
        UserEntity user = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType).orElse(null);
        if (user != null) {
            final String accessToken = JwtTokenUtils.generateAccessToken(socialId, socialType, jwtKey);
            return UserSignInResponse.builder()
                    .isSigned("true")
                    .accessToken(accessToken)
                    .refreshToken(user.getTokenEntity().getRefreshToken())
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

        // 회원 가입 이후에 FCM 토큰을 따로 저장한다.
        TokenEntity tokenEntity = TokenEntity.builder().refreshToken(refreshToken).build();
        tokenEntityRepository.save(tokenEntity);

        entity = UserEntity.SignUpDtoToEntity()
                .request(request)
                .tokenEntity(tokenEntity)
                .build();

        userEntityRepository.save(entity);

        return UserSignUpResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean findByNickname(String nickname) {
        UserEntity entity = userEntityRepository.findByNickname(nickname).orElse(null);
        return entity != null ? true : false;
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        final String socialId = user.getSocialId();
        final SocialType socialType = user.getSocialType();
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        TokenEntity tokenEntity = tokenEntityRepository.findById(userEntity.getId())
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_TOKEN));

        return UserInfoResponse.builder()
                .userId(userEntity.getId())
                .socialId(userEntity.getSocialId())
                .socialType(userEntity.getSocialType())
                .role(userEntity.getRole())
                .email(userEntity.getEmail())
                .nickname(userEntity.getNickname())
                .profileUrl(userEntity.getProfileUrl())
                .content(userEntity.getContent())
                .alarmYn(userEntity.getAlarmYn())
                .accuseCnt(userEntity.getAccuseCnt())
                .fcmToken(tokenEntity.getFcmToken())
                .refreshToken(tokenEntity.getRefreshToken())
                .registerAt(userEntity.getRegisterAt())
                .build();
    }

    @Transactional
    public void postFcmToken(String fcmToken, Authentication authentication) {
        if (!StringUtils.hasLength(fcmToken)) {
            throw new DodalApplicationException(ErrorCode.INVALID_TOKEN);
        }
        UserEntity userEntity = userToUserEntity(authentication);
        TokenEntity tokenEntity = userEntity.getTokenEntity();
        tokenEntity.updateFcmToken(fcmToken);
        tokenEntityRepository.save(tokenEntity);
    }

    @Transactional
    public UserAccessTokenResponse postAccessToken(Authentication authentication) {
        UserEntity userEntity = userToUserEntity(authentication);
        String accessToken = JwtTokenUtils.generateAccessToken(userEntity.getSocialId(), userEntity.getSocialType(), jwtKey);
        return UserAccessTokenResponse.builder().accessToken(accessToken).build();
    }

    @Transactional
    public void deleteUser(Authentication authentication) {
        UserEntity userEntity = userToUserEntity(authentication);
        userEntityRepository.delete(userEntity);
    }

    @Transactional(readOnly = true)
    protected UserEntity userToUserEntity(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        final String socialId = user.getSocialId();
        final SocialType socialType = user.getSocialType();
        return userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }



}
