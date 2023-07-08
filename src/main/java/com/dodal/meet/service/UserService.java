package com.dodal.meet.service;


import com.dodal.meet.controller.request.user.UserProfileRequest;
import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.controller.request.user.UserSignInRequest;
import com.dodal.meet.controller.request.user.UserUpdateRequest;
import com.dodal.meet.controller.response.category.TagResponse;
import com.dodal.meet.controller.response.user.*;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.TagEntity;
import com.dodal.meet.model.entity.TokenEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.model.entity.UserTagEntity;
import com.dodal.meet.repository.TagEntityRepository;
import com.dodal.meet.repository.TokenEntityRepository;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.repository.UserTagEntityRepository;
import com.dodal.meet.utils.DtoUtils;
import com.dodal.meet.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final TagEntityRepository tagEntityRepository;
    private final UserTagEntityRepository userTagEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final TokenEntityRepository tokenEntityRepository;

    private final ImageService imageService;

    @Value("${jwt.secret-key}")
    private String jwtKey;

    @Transactional
    public UserSignInResponse signIn(final UserSignInRequest request) {
        final String socialId = request.getSocialId();
        final SocialType socialType = request.getSocialType();
        final UserEntity user = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType).orElse(null);
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
    public User findBySocialIdAndSocialTypeToUser(final String socialId, final SocialType socialType) {
        return userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .map(User::fromEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }

    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequest request, final MultipartFile profile) {

        validNickname(request.getNickname());

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
        final TokenEntity tokenEntity = TokenEntity.builder().refreshToken(refreshToken).build();
        tokenEntityRepository.save(tokenEntity);

        // 이미지 정보가 있는 경우 S3 버킷 저장 및 URL 반환
        if (profile != null) {
            String profileUrl = imageService.uploadImage(new UserProfileRequest(profile)).getProfileUrl();
            request.setProfileUrl(profileUrl);
        }

        final UserEntity newUserEntity = UserEntity.SignUpDtoToEntity()
                .request(request)
                .tokenEntity(tokenEntity)
                .build();

        userEntityRepository.save(newUserEntity);

        List<UserTagEntity> userTagEntities = new ArrayList<>();
        request.getTagList().forEach(tagValue -> {
            TagEntity tagEntity = tagEntityRepository.findByValue(tagValue).orElseThrow(
                    () -> new DodalApplicationException(ErrorCode.INVALID_TAG_LIST_FIELD));
            userTagEntities.add(UserTagEntity.tagEntityToUserTagEntity(newUserEntity, tagEntity));
            }
        );

        userTagEntityRepository.saveAll(userTagEntities);

        return UserSignUpResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }



    @Transactional(readOnly = true)
    public boolean findByNickname(String nickname) {
        validNickname(nickname);
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
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_JWT_TOKEN));
        List<UserTagEntity> userTagList = userTagEntityRepository.findAllByUserEntity(userEntity);

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
                .tagList(TagResponse.userEntitesToList(userTagList))
                .fcmToken(tokenEntity.getFcmToken())
                .refreshToken(tokenEntity.getRefreshToken())
                .registerAt(userEntity.getRegisterAt())
                .build();
    }

    @Transactional
    public UserInfoResponse updateUser(final UserUpdateRequest userUpdateRequest, final MultipartFile profile, Authentication authentication) {
        UserInfoResponse userInfo = getUser(authentication);

        UserEntity userEntity = userEntityRepository.findById(userInfo.getUserId()).orElse(null);

        final String requestNickname = userUpdateRequest.getNickname();
        final String requestContent = userUpdateRequest.getContent();
        final List<String> requestTagList = userUpdateRequest.getTagList();

        if (requestNickname != null && userInfo.getNickname() != userUpdateRequest.getNickname()) {
            userEntity.updateNickname(requestNickname);
        }

        if (requestContent != null && userInfo.getContent() != userUpdateRequest.getContent()) {
            userEntity.updateContent(requestContent);
        }

        if (profile != null) {
            String profileUrl = imageService.uploadImage(new UserProfileRequest(profile)).getProfileUrl();
            userEntity.updateProfileUrl(profileUrl);
        }

        if (!requestTagList.isEmpty()) {
            List<UserTagEntity> userTagEntity = userTagEntityRepository.findAllByUserEntity(userEntity);
            userTagEntityRepository.deleteAll(userTagEntity);

            List<UserTagEntity> userTagEntities = new ArrayList<>();
            requestTagList.forEach(tagValue -> {
                        TagEntity tagEntity = tagEntityRepository.findByValue(tagValue).orElseThrow(
                                () -> new DodalApplicationException(ErrorCode.INVALID_TAG_LIST_FIELD));
                        userTagEntities.add(UserTagEntity.tagEntityToUserTagEntity(userEntity, tagEntity));
                    }
            );

            userTagEntityRepository.saveAll(userTagEntities);
        }

        return getUser(authentication);
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
    public UserEntity userToUserEntity(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        final String socialId = user.getSocialId();
        final SocialType socialType = user.getSocialType();
        return userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }

    private void validNickname(String nickname) {
        String trimNickname = nickname.trim();
        if (trimNickname.length() == 0 || trimNickname == null) {
            throw new DodalApplicationException(ErrorCode.INVALID_NICKNAME_FIELD);
        }
    }


}
