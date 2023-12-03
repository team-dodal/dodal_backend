package com.dodal.meet.service;

import com.dodal.meet.controller.request.user.*;
import com.dodal.meet.controller.response.CommonCodeResponse;
import com.dodal.meet.controller.response.feed.FeedCustom;
import com.dodal.meet.controller.response.user.*;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.DtoUtils;
import com.dodal.meet.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final CategoryEntityRepository categoryEntityRepository;
    private final FeedCustom challengeFeedEntityRepository;
    private final CommonCodeEntityRepository commonCodeEntityRepository;
    private final AccuseEntityRepository accuseEntityRepository;
    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;
    private final ChallengeUserEntityRepository challengeUserEntityRepository;
    private final TagEntityRepository tagEntityRepository;
    private final UserTagEntityRepository userTagEntityRepository;
    private final UserTagBulkRepository userTagBulkRepository;
    private final UserEntityRepository userEntityRepository;
    private final TokenEntityRepository tokenEntityRepository;
    private final ImageService imageService;
    private final UserCacheRepository userCacheRepository;
    @Value("${jwt.secret-key}")
    private String jwtKey;
    @Value("${spring.config.activate.on-profile}")
    private String profile;

    @Transactional
    public UserSignInResponse signIn(final UserSignInRequest request) {
        final String socialId = request.getSocialId();
        final SocialType socialType = request.getSocialType();
        final UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(socialId, socialType).orElse(null);

        if (!ObjectUtils.isEmpty(userEntity)) {
            final String accessToken = JwtTokenUtils.generateAccessToken(socialId, socialType, jwtKey);
            final String refreshToken = JwtTokenUtils.generateRefreshToken(socialId, socialType, jwtKey);
            final List<UserTagEntity> userTagEntityList = userTagEntityRepository.findAllByUserEntity(userEntity);

            // 가입한 유저가 로그인한 경우 리프레시 토큰을 업데이트한다.
            TokenEntity tokenEntity = tokenEntityRepository.findById(userEntity.getTokenEntity().getId()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_TOKEN));
            tokenEntity.updateRefreshToken(refreshToken);

            final List<String> userTagValueList = userTagEntityList.stream().map(t -> t.getTagValue()).collect(Collectors.toList());
            final List<TagEntity> tagEntityList = tagEntityRepository.findAllByTagValueList(userTagValueList);
            // FETCH JOIN
            final List<CategoryEntity> categoryEntityList = categoryEntityRepository.findAllByTagEntity(tagEntityList);

            if (!profile.equals("test")){
                userCacheRepository.setUser(loadUserBySocialIdAndSocialType(socialId, socialType));
            }
            return UserSignInResponse.newInstance(userEntity, accessToken, refreshToken, tagEntityList, categoryEntityList);
        }
        return UserSignInResponse.newInstance();
    }




    @Transactional
    public UserSignUpResponse signUp(final UserSignUpRequest request) {
        // 닉네임 공백 제거
        final String nickname = StringUtils.trim(request.getNickname());
        request.setNickname(nickname);

        final String socialId = request.getSocialId();
        final SocialType socialType = request.getSocialType();

        // 이미 가입한 회원인 경우 예외 반환
        checkIfAlreadySignedUp(socialId, socialType);

        // JWT 토큰 발행 및 엔티티 저장
        final String accessToken = JwtTokenUtils.generateAccessToken(socialId, socialType, jwtKey);
        final String refreshToken = JwtTokenUtils.generateRefreshToken(socialId, socialType, jwtKey);
        final TokenEntity tokenEntity = saveRefreshToken(refreshToken);
        final UserEntity userEntity = saveUserEntity(request, tokenEntity);

        final List<TagEntity> tagEntityList = tagEntityRepository.findAllByTagValueList(request.getTagList());
        final List<UserTagEntity> userTagEntityList = saveUserTags(tagEntityList, userEntity);

        // FETCH JOIN
        final List<CategoryEntity> categoryEntityList = categoryEntityRepository.findAllByTagEntity(tagEntityList);

        final UserInfoResponse userInfoResponse = UserInfoResponse.newInstance(userEntity,userTagEntityList, categoryEntityList);
        return UserSignUpResponse.newInstance(userInfoResponse, accessToken);
    }

    private void checkIfAlreadySignedUp(final String socialId, final SocialType socialType) {
        userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .ifPresent(it -> {
                    throw new DodalApplicationException(ErrorCode.INVALID_SIGNUP_REQUEST);
                });
    }

    private TokenEntity saveRefreshToken(final String refreshToken) {
        final TokenEntity tokenEntity = TokenEntity.builder().refreshToken(refreshToken).build();
        return tokenEntityRepository.save(tokenEntity);
    }

    private UserEntity saveUserEntity(final UserSignUpRequest request, final TokenEntity tokenEntity) {
        return userEntityRepository.save(UserEntity.newInstance(request, tokenEntity));
    }

    private List<UserTagEntity> saveUserTags(final List<TagEntity> tagEntityList, final UserEntity userEntity) {
        final List<UserTagEntity> userTagEntityList = tagEntityList.stream()
                .map(tagEntity -> UserTagEntity.newInstance(userEntity, tagEntity))
                .collect(Collectors.toList());

        // BULK INSERT 작업 - JdbcTemplate
        userTagBulkRepository.saveAll(userTagEntityList);
        return userTagEntityList;
    }


    @Transactional(readOnly = true)
    public boolean findByNickname(final String nickname) {
        final UserEntity entity = userEntityRepository.findByNickname(nickname).orElse(null);
        return entity != null ? Boolean.TRUE : Boolean.FALSE;
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUser(final User user) {
        final UserEntity userEntity = userToUserEntity(user);
        return getUserInfo(userEntity);
    }

    private UserInfoResponse getUserInfo(final UserEntity userEntity) {
        final List<UserTagEntity> userTagEntityList = userTagEntityRepository.findAllByUserEntity(userEntity);
        final List<String> userTagValueList = userTagEntityList.stream().map(t -> t.getTagValue()).collect(Collectors.toList());
        final List<TagEntity> tagEntityList = tagEntityRepository.findAllByTagValueList(userTagValueList);
        // FETCH JOIN
        final List<CategoryEntity> categoryEntityList = categoryEntityRepository.findAllByTagEntity(tagEntityList);
        return UserInfoResponse.newInstance(userEntity,userTagEntityList, categoryEntityList);
    }

    @Transactional
    public UserInfoResponse updateUser(final UserUpdateRequest userUpdateRequest, final User user) {
        // TODO : REDIS CACHE EVICT
        UserEntity userEntity = userToUserEntity(user);
        final String requestNickname = userUpdateRequest.getNickname();
        final String requestContent = userUpdateRequest.getContent();
        final List<String> requestTagValueList = userUpdateRequest.getTagList();
        final String beforeProfileUrl = userEntity.getProfileUrl();

        userEntity.updateNickname(requestNickname);
        userEntity.updateContent(requestContent);
        userEntity.updateProfileUrl(userUpdateRequest.getProfileUrl());

        // 직전 이미지가 S3에 등록되어 있으면서, 요청 온 이미지 URL이 다를 경우 직전 이미지 삭제
        if (beforeProfileUrl != null && beforeProfileUrl != userUpdateRequest.getProfileUrl()) {
            imageService.deleteImg(beforeProfileUrl);
        }

        // 방장으로 운영중인 도전방이 있는 경우 도전방 정보 업데이트
        challengeRoomEntityRepository.updateNicknameAndProfileUrlByHostUserId(userEntity.getId(), requestNickname, userUpdateRequest.getProfileUrl());

        final List<UserTagEntity> userTagEntityList = userTagEntityRepository.findAllByUserEntity(userEntity);
        // BULK DELETE
        userTagBulkRepository.deleteAll(userTagEntityList);

        List<TagEntity> tagEntityList = tagEntityRepository.findAllByTagValueList(requestTagValueList);
        if (requestTagValueList.size() != tagEntityList.size()) {
            throw new DodalApplicationException(ErrorCode.INVALID_TAG_LIST_FIELD);
        }
        saveUserTags(tagEntityList, userEntity);

        return getUserInfo(userEntity);
    }

    @Transactional
    public void postFcmToken(final String fcmToken, final User user) {
        if (StringUtils.isEmpty(fcmToken)) {
            throw new DodalApplicationException(ErrorCode.INVALID_TOKEN);
        }
        final UserEntity userEntity = userToUserEntity(user);
        TokenEntity tokenEntity = userEntity.getTokenEntity();
        tokenEntity.updateFcmToken(fcmToken);
        tokenEntityRepository.save(tokenEntity);
    }

    @Transactional
    public UserAccessTokenResponse postAccessToken(final User user) {
        final UserEntity userEntity = userToUserEntity(user);
        final String accessToken = JwtTokenUtils.generateAccessToken(userEntity.getSocialId(), userEntity.getSocialType(), jwtKey);
        return UserAccessTokenResponse.newInstance(accessToken);
    }

    @Transactional
    public void deleteUser(final User user) {
        final UserEntity userEntity = userToUserEntity(user);

        List<ChallengeRoomEntity> hostRoomList = challengeRoomEntityRepository.findAllByHostId(userEntity.getId());
        if (!CollectionUtils.isEmpty(hostRoomList)) {
            throw new DodalApplicationException(ErrorCode.ROOM_DELETE_REQUIRED);
        }
        challengeRoomEntityRepository.updateUserCntByDeleteUser(userEntity.getId());

        userEntityRepository.delete(userEntity);
    }

    @Transactional(readOnly = true)
    public UserEntity userToUserEntity(User user) {
        // TODO : REDIS CACHE
        final String socialId = user.getSocialId();
        final SocialType socialType = user.getSocialType();
        return userEntityRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }

    public User loadUserBySocialIdAndSocialType(String socialId, SocialType socialType) {
        return userCacheRepository.getUser(socialId, socialType).orElseGet(() ->
                userEntityRepository.findBySocialIdAndSocialType(socialId, socialType).map(User::fromEntity)
                        .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST)));
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(final User user) {
        final UserEntity userEntity = userToUserEntity(user);
        final List<UserTagEntity> userTagEntityList = userTagEntityRepository.findAllByUserEntity(userEntity);

        final List<String> userTagValueList = userTagEntityList.stream().map(t -> t.getTagValue()).collect(Collectors.toList());
        final List<TagEntity> tagEntityList = tagEntityRepository.findAllByTagValueList(userTagValueList);
        // FETCH JOIN
        final List<CategoryEntity> categoryEntityList = categoryEntityRepository.findAllByTagEntity(tagEntityList);

        final List<ChallengeUserEntity> challengeUserInfo = challengeUserEntityRepository.findAllByUserId(userEntity.getId());

        /*
            가입한 도전방 리스트의 경우 방장이 도전방 제목을 변경했어도 유저가 피드를 올리기 전까지 이전 도전방 제목으로 보여지도록 한다.
            피드에 저장된 마지막 도전방 제목 정보가 있으면 해당 제목으로 보여지도록 한다.
         */
        final Map<Integer, String> latestChallengeRoomTitles = challengeFeedEntityRepository.findMaxDateFeedByUserId(userEntity.getId())
                .stream().collect(Collectors.toMap(ChallengeFeedEntity::getRoomId, ChallengeFeedEntity::getRoomTitle));

        final List<ChallengeRoomResponse> challengeRoomList = challengeUserInfo.stream()
                .map(dto -> ChallengeRoomResponse.builder()
                        .roomId(dto.getChallengeRoomEntity().getId())
                        .title(getChallengeRoomTitle(dto, latestChallengeRoomTitles))
                        .build())
                .collect(Collectors.toList());

        final UserRoomCertInfo userRoomCertInfo = challengeUserEntityRepository.findMaxCertInfoByUserId(userEntity.getId());
        return MyPageResponse.newInstance(userEntity, categoryEntityList, userTagEntityList, challengeRoomList, userRoomCertInfo);
    }

    private String getChallengeRoomTitle(final ChallengeUserEntity challengeUserEntity, final Map<Integer, String> latestChallengeRoomTitles) {
        final int roomId = challengeUserEntity.getChallengeRoomEntity().getId();
        // 피드에 저장된 가장 최신의 도전방 제목이 있으면 그것을 반환하고, 없으면 도전방의 기본 제목을 반환
        return latestChallengeRoomTitles.getOrDefault(roomId, challengeUserEntity.getChallengeRoomEntity().getTitle());
    }

    @Transactional(readOnly = true)
    public MyPageCalenderResponse getMyPageCalendarInfo(final Integer roomId, final String dateYM, final User user) {
        final UserEntity userEntity = userToUserEntity(user);
        challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        return challengeRoomEntityRepository.getMyPageCalendarInfo(roomId, dateYM, userEntity.getId());
    }

    @Transactional
    public void postAccuseUser(final Long targetUserId, final UserAccuseRequest request, final User sourceUser) {
        final List<CommonCodeEntity> codeEntityList = commonCodeEntityRepository.findAllByCategory("ACCUSE");
        if (CollectionUtils.isEmpty(codeEntityList)) {
            throw new DodalApplicationException(ErrorCode.NOT_FOUND_COMMON_CODE);
        }

        final List<String> codeList = codeEntityList.stream().map(e -> e.getCode()).collect(Collectors.toList());

        // 공통코드에 없는 코드 값이 있는 경우 예외 반환
        if (!codeList.contains(request.getAccuseCode())) {
            throw new DodalApplicationException(ErrorCode.NOT_FOUND_ACCUSE_CODE);
        }

        final UserEntity userEntity = userToUserEntity(sourceUser);
        UserEntity targetUserEntity = userEntityRepository.findById(targetUserId).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));

        // 과거에 신고한 이력이 있었는 지 확인
        final AccuseEntity beforeAccuseEntity = accuseEntityRepository.findBySourceUserIdAndTargetUserId(sourceUser.getId(), targetUserId);
        if (!ObjectUtils.isEmpty(beforeAccuseEntity)) {
            throw new DodalApplicationException(ErrorCode.ALREADY_ACCUSE_SUCCEED);
        }

        if (userEntity.getId() == targetUserId) {
            throw new DodalApplicationException(ErrorCode.INVALID_USER_ACCUSE);
        }

        // 신고 문항이 7번인 경우 주관식 응답 요청이 와야한다.
        if ( (request.getAccuseCode().equals("007") && StringUtils.isEmpty(request.getContent())) ||
            !request.getAccuseCode().equals("007") && !StringUtils.isEmpty(request.getContent())
        ) {
            throw new DodalApplicationException(ErrorCode.INVALID_ACCUSE_REQUEST);
        }

        final AccuseEntity accuseEntity = AccuseEntity.newInstance(request, targetUserId, userEntity);
        accuseEntityRepository.save(accuseEntity);

        // 피신고자 신고 횟수 업데이트
        targetUserEntity.updateAccuseCnt(DtoUtils.ONE);
    }

    @Transactional(readOnly = true)
    public CommonCodeResponse getAccuseCode() {
        List<CommonCodeEntity> codeList = commonCodeEntityRepository.findAllByCategory("ACCUSE");
        return CommonCodeResponse.newInstance(codeList);
    }
}
