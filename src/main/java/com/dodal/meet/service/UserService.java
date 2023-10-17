package com.dodal.meet.service;


import com.dodal.meet.controller.request.user.UserProfileRequest;
import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.controller.request.user.UserSignInRequest;
import com.dodal.meet.controller.request.user.UserUpdateRequest;
import com.dodal.meet.controller.response.category.TagResponse;
import com.dodal.meet.controller.response.category.UserCategoryResponse;
import com.dodal.meet.controller.response.user.*;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.repository.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;
    private final ChallengeUserEntityRepository challengeUserEntityRepository;
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
            final String refreshToken = JwtTokenUtils.generateRefreshToken(socialId, socialType, jwtKey);
            List<UserTagEntity> userTagEntity = userTagEntityRepository.findAllByUserEntity(user);
            TokenEntity tokenEntity = tokenEntityRepository.findByUserEntity(user).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_TOKEN));
            tokenEntity.updateRefreshToken(refreshToken);
            tokenEntityRepository.save(tokenEntity);

            List<String> userTagValueList = new ArrayList<>();
            userTagEntity.forEach(entity -> userTagValueList.add(entity.getTagValue()));

            List<TagEntity> tagList = tagEntityRepository.findAllByUserTagValue(userTagValueList);

            Set<CategoryEntity> categorySet = new HashSet<>();
            tagList.forEach(e -> categorySet.add(e.getCategoryEntity()));

            List<CategoryEntity> categoryList = new ArrayList<>(categorySet);

            return UserSignInResponse.builder()
                    .isSigned("true")
                    .userId(user.getId())
                    .socialId(user.getSocialId())
                    .socialType(user.getSocialType())
                    .role(user.getRole())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileUrl(user.getProfileUrl())
                    .content(user.getContent())
                    .categoryList(UserCategoryResponse.fromEntityList(categoryList))
                    .tagList(TagResponse.tagEntitiesToList(tagList))
                    .alarmYn(user.getAlarmYn())
                    .accuseCnt(user.getAccuseCnt())
                    .fcmToken(user.getTokenEntity().getFcmToken())
                    .registerAt(user.getRegisterAt())
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
    public User findBySocialIdAndSocialTypeToUser(final String socialId, final SocialType socialType) {
        return userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .map(User::fromEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }

    @Transactional
    public UserSignUpResponse signUp(final UserSignUpRequest request, final MultipartFile profile) {

        validNickname(request.getNickname());

        final String socialId = request.getSocialId();
        final SocialType socialType = request.getSocialType();

        // 회원가입되어 있는 지 체크
        UserEntity entity = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType).orElse(null);
        if (entity != null) {
            throw new DodalApplicationException(ErrorCode.INVALID_SIGNUP_REQUEST);
        }

        // JWT 토큰 발행 (액세스 토큰 / 리프레시 토큰)
        final String accessToken = JwtTokenUtils.generateAccessToken(socialId, socialType, jwtKey);
        final String refreshToken = JwtTokenUtils.generateRefreshToken(socialId, socialType, jwtKey);

        // 회원 가입 이후에 FCM 토큰을 따로 저장한다.
        final TokenEntity tokenEntity = TokenEntity.builder().refreshToken(refreshToken).build();
        tokenEntityRepository.save(tokenEntity);

        // 이미지 정보가 있는 경우 S3 버킷 저장 및 URL 반환
        if (profile != null) {
            String profileUrl = imageService.uploadProfileImg(new UserProfileRequest(profile)).getProfileUrl();
            request.setProfileUrl(profileUrl);
        }

        final UserEntity newUserEntity = UserEntity.SignUpDtoToEntity()
                .request(request)
                .tokenEntity(tokenEntity)
                .build();

        userEntityRepository.save(newUserEntity);

        List<UserTagEntity> userTagEntities = new ArrayList<>();
        request.getTagList().forEach(tagValue -> {
            TagEntity tagEntity = tagEntityRepository.findByTagValue(tagValue).orElseThrow(
                    () -> new DodalApplicationException(ErrorCode.INVALID_TAG_LIST_FIELD));
            userTagEntities.add(UserTagEntity.tagEntityToUserTagEntity(newUserEntity, tagEntity));
            }
        );

        userTagEntityRepository.saveAll(userTagEntities);

        UserInfoResponse userInfoResponse = entityToUserInfo(newUserEntity, tokenEntity, userTagEntities);

        return UserSignUpResponse.convertUserInfoToUserSignUp(userInfoResponse, accessToken);
    }


    @Transactional(readOnly = true)
    public boolean findByNickname(String nickname) {
        validNickname(nickname);
        UserEntity entity = userEntityRepository.findByNickname(nickname).orElse(null);
        return entity != null ? true : false;
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUser(final Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        final String socialId = user.getSocialId();
        final SocialType socialType = user.getSocialType();
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        TokenEntity tokenEntity = tokenEntityRepository.findByUserEntity(userEntity)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_JWT_TOKEN));
        List<UserTagEntity> userTagList = userTagEntityRepository.findAllByUserEntity(userEntity);

        return entityToUserInfo(userEntity, tokenEntity, userTagList);
    }

    @Transactional
    public UserInfoResponse updateUser(final UserUpdateRequest userUpdateRequest, final MultipartFile profile, final String profileUrl, Authentication authentication) {
        final UserInfoResponse userInfo = getUser(authentication);

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

        final String beforeProfileUrl = userEntity.getProfileUrl();

        if (profile != null && profileUrl != null) {
            // 기존 이미지 요청과 새로운 이미지 요청이 동시에 오는 경우 throw
            throw new DodalApplicationException(ErrorCode.ILLEGAL_IMAGE_REQUEST);
        } else if (profile == null && profileUrl == null) {
            // 기존 이미지(null)로 변경
            if (beforeProfileUrl != null) {
                imageService.deleteImg(beforeProfileUrl);
                userEntity.updateProfileUrl(null);
            }
        } else if (profile != null) {
            // 기존에 등록된 S3 이미지가 있으면 제거
            if (beforeProfileUrl != null) {
                imageService.deleteImg(beforeProfileUrl);
            }
            String newProfileUrl = imageService.uploadProfileImg(new UserProfileRequest(profile)).getProfileUrl();
            userEntity.updateProfileUrl(newProfileUrl);
        } else if (profileUrl != null) {
            // 기존에 등록된 이미지와 요청온 이미지가 다르면 throw
            if (beforeProfileUrl != null && !beforeProfileUrl.equals(profileUrl)) {
                throw new DodalApplicationException(ErrorCode.INVALID_IMAGE_URL);
            }
        }

        if (!requestTagList.isEmpty()) {
            List<UserTagEntity> userTagEntity = userTagEntityRepository.findAllByUserEntity(userEntity);
            userTagEntityRepository.deleteAll(userTagEntity);

            List<UserTagEntity> userTagEntities = new ArrayList<>();
            requestTagList.forEach(tagValue -> {
                        TagEntity tagEntity = tagEntityRepository.findByTagValue(tagValue).orElseThrow(
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

    private UserInfoResponse entityToUserInfo(UserEntity userEntity, TokenEntity tokenEntity, List<UserTagEntity> userTagEntities) {
        List<String> userTagValueList = new ArrayList<>();
        userTagEntities.forEach(entity -> userTagValueList.add(entity.getTagValue()));

        List<TagEntity> tagList = tagEntityRepository.findAllByUserTagValue(userTagValueList);

        Set<CategoryEntity> categorySet = new HashSet<>();
        tagList.forEach(e -> categorySet.add(e.getCategoryEntity()));

        List<CategoryEntity> categoryList = new ArrayList<>(categorySet);
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
                .categoryList(UserCategoryResponse.fromEntityList(categoryList))
                .tagList(TagResponse.userEntitesToList(userTagEntities))
                .fcmToken(tokenEntity.getFcmToken())
                .refreshToken(tokenEntity.getRefreshToken())
                .registerAt(userEntity.getRegisterAt())
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(User user) {
        final String socialId = user.getSocialId();
        final SocialType socialType = user.getSocialType();
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        List<UserTagEntity> userTagList = userTagEntityRepository.findAllByUserEntity(userEntity);

        List<String> userTagValueList = new ArrayList<>();
        userTagList.forEach(entity -> userTagValueList.add(entity.getTagValue()));

        List<TagEntity> tagList = tagEntityRepository.findAllByUserTagValue(userTagValueList);

        Set<CategoryEntity> categorySet = new HashSet<>();
        tagList.forEach(e -> categorySet.add(e.getCategoryEntity()));
        List<CategoryEntity> categoryList = new ArrayList<>(categorySet);

        List<ChallengeUserEntity> challengeUserInfo = challengeUserEntityRepository.findAllByUserId(userEntity.getId());

        List<ChallengeRoomResponse> challengeRoomList = new ArrayList<>();

        challengeUserInfo.forEach(dto -> challengeRoomList.add(ChallengeRoomResponse.builder()
                .roomId(dto.getChallengeRoomEntity().getId())
                .title(dto.getChallengeRoomEntity().getTitle())
                .build()));

        UserRoomCertInfo userRoomCertInfo = challengeUserEntityRepository.findMaxCertInfoByUserId(userEntity.getId());
        return MyPageResponse.builder()
                .userId(userEntity.getId())
                .nickname(userEntity.getNickname())
                .profileUrl(userEntity.getProfileUrl())
                .content(userEntity.getContent())
                .categoryList(UserCategoryResponse.fromEntityList(categoryList))
                .tagList(TagResponse.userEntitesToList(userTagList))
                .challengeRoomList(challengeRoomList)
                .maxContinueCertCnt(userRoomCertInfo.getMaxContinueCertCnt())
                .totalCertCnt(userRoomCertInfo.getTotalCertCnt())
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageCalenderResponse getMyPageCalendarInfo(Integer roomId, String dateYM, User user) {
        final String socialId = user.getSocialId();
        final SocialType socialType = user.getSocialType();
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));

        return challengeRoomEntityRepository.getMyPageCalendarInfo(roomId, dateYM, userEntity.getId());
    }
}
