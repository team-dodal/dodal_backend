package com.dodal.meet.service;

import com.dodal.meet.controller.request.challengeroom.*;
import com.dodal.meet.controller.response.challengeroom.ChallengeCreateResponse;
import com.dodal.meet.controller.response.challengeroom.ChallengeNotiResponse;
import com.dodal.meet.controller.response.challengeroom.ChallengeRoomDetailResponse;
import com.dodal.meet.controller.response.challengeroom.ChallengeRoomSearchResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.RoomSearchType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.MessageType;
import com.dodal.meet.utils.MessageUtils;
import com.dodal.meet.utils.UserUtils;
import com.dodal.meet.utils.ValueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.util.ObjectUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeRoomService {
    private final ChallengeNotiEntityRepository challengeNotiEntityRepository;
    private final ChallengeFeedEntityRepository challengeFeedEntityRepository;
    private final CategoryEntityRepository categoryEntityRepository;
    private final ChallengeBookmarkEntityRepository challengeBookmarkEntityRepository;
    private final UserEntityRepository userEntityRepository;

    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;
    private final ChallengeUserEntityRepository challengeUserEntityRepository;
    private final ChallengeTagEntityRepository challengeTagEntityRepository;
    private final TagEntityRepository tagEntityRepository;

    private final UserService userService;
    private final ImageService imageService;
    private final FcmPushService fcmPushService;


    @Transactional
    public ChallengeCreateResponse createChallengeRoom(final ChallengeRoomCreateRequest challengeRoomCreateRequest, final Authentication authentication) {

        ChallengeRoomEntity challengeRoomEntity = ChallengeRoomEntity.dtoToEntity(challengeRoomCreateRequest);
        String thumbnailImgUrl = null;
        if (challengeRoomEntity.getThumbnailImgUrl() != null) {
            thumbnailImgUrl = imageService.uploadMultipartFile(challengeRoomCreateRequest.getThumbnailImg());
        }
        final String certCorrectImgUrl = imageService.uploadMultipartFile(challengeRoomCreateRequest.getCertCorrectImg());
        final String certWrongImgUrl = imageService.uploadMultipartFile(challengeRoomCreateRequest.getCertWrongImg());
        final String tagValue = challengeRoomCreateRequest.getTagValue();


        UserEntity userEntity = userService.userToUserEntity(authentication);
        ChallengeUserEntity challengeUserEntity = ChallengeUserEntity.getHostEntity(userEntity);

        challengeRoomEntity.updateImgUrl(thumbnailImgUrl, certCorrectImgUrl, certWrongImgUrl);
        challengeRoomEntity.updateUserInfo(userEntity);
        challengeRoomEntityRepository.save(challengeRoomEntity);

        challengeUserEntity.addChallengeRoomEntity(challengeRoomEntity);
        challengeUserEntityRepository.save(challengeUserEntity);

        TagEntity tagEntity = tagEntityRepository.findByTagValue(tagValue).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_TAG));
        ChallengeTagEntity challengeTagEntity = getChallengeTagEntity(challengeRoomEntity, tagEntity);
        challengeRoomEntity.addChallengeTagEntity(challengeTagEntity);
        challengeTagEntityRepository.save(challengeTagEntity);

        return getChallengeCreateRequestFromEntities(challengeRoomEntity, challengeTagEntity, challengeUserEntity);
    }

    @Transactional
    public Page<ChallengeRoomSearchResponse> getChallengeRooms(String condition, String tagValue, Pageable pageable, Authentication authentication) {
        User user = UserUtils.getUserInfo(authentication);
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        if (!isEmpty(tagValue)) {
            validValue(ValueType.TAG, tagValue);
        }
        ChallengeRoomCondition challengeRoomCondition = ChallengeRoomCondition
                .builder()
                .roomSearchType(RoomSearchType.of(condition.toUpperCase()))
                .tagValue(tagValue)
                .build();

        return challengeRoomEntityRepository.getChallengeRooms(challengeRoomCondition, pageable, userEntity);
    }

    @Transactional(readOnly = true)
    public Page<ChallengeRoomSearchResponse> getChallengeRoomsByCategory(ChallengeRoomSearchCategoryRequest request, Pageable pageable, Authentication authentication) {
        User user = UserUtils.getUserInfo(authentication);
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        final String categoryValue = request.getCategoryValue();
        final String tagValue = request.getTagValue();
        if (!isEmpty(categoryValue)) {
            validValue(ValueType.CATEGORY, categoryValue);
        }
        if (!isEmpty(tagValue)) {
            validValue(ValueType.TAG, tagValue);
        }
        return challengeRoomEntityRepository.getChallengeRoomsByCategory(request, pageable, userEntity);
    }

    @Transactional
    public void createBookmark(Integer roomId, Authentication authentication) {
        updateBookmark(roomId, authentication, "CREATE");
    }

    @Transactional
    public void deleteBookmark(Integer roomId, Authentication authentication) {
        updateBookmark(roomId, authentication, "DELETE");
    }

    @Transactional(readOnly = true)
    public ChallengeRoomDetailResponse getChallengeRoomDetail(Integer roomId, Authentication authentication) {
        UserEntity userEntity = getUserEntityByAuthentication(authentication);
        getChallengeRoomEntityById(roomId);
        return challengeRoomEntityRepository.getChallengeRoomDetail(roomId,userEntity);
    }

    @Transactional
    public void joinChallengeRoom(Integer roomId, Authentication authentication) {

        ChallengeRoomEntity challengeRoom = getChallengeRoomEntityById(roomId);
        UserEntity userEntity = getUserEntityByAuthentication(authentication);
        ChallengeUserEntity challengeUser = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), challengeRoom).orElse(null);
        // 이미 가입된 회원이라면
        if (!isEmpty(challengeUser)) {
            throw new DodalApplicationException(ErrorCode.INVALID_ROOM_JOIN);
        }
        ChallengeUserEntity challengeUserEntity = ChallengeUserEntity.builder()
                .challengeRoomEntity(challengeRoom)
                .roomRole(RoomRole.USER)
                .certCnt(0)
                .userId(userEntity.getId())
                .nickname(userEntity.getNickname())
                .build();
        challengeUserEntityRepository.save(challengeUserEntity);
        challengeRoom.updateUserCnt(1);
        challengeRoomEntityRepository.save(challengeRoom);
    }

    @Transactional
    public void leaveChallengeRoom(Integer roomId, Authentication authentication) {
        ChallengeRoomEntity challengeRoom = getChallengeRoomEntityById(roomId);
        UserEntity userEntity = getUserEntityByAuthentication(authentication);
        ChallengeUserEntity challengeUser = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), challengeRoom)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_ROOM_LEAVE));
        challengeUserEntityRepository.delete(challengeUser);
        challengeRoom.updateUserCnt(-1);
        challengeRoomEntityRepository.save(challengeRoom);
    }

    @Transactional
    public void createCertification(final Integer roomId, final MultipartFile certificationImg, final String content, final Authentication authentication) {
        ChallengeRoomEntity challengeRoom = getChallengeRoomEntityById(roomId);
        UserEntity userEntity = getUserEntityByAuthentication(authentication);
        ChallengeUserEntity challengeUser = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), challengeRoom).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        String certImgUrl = imageService.uploadMultipartFile(certificationImg);
        ChallengeFeedEntity entity = ChallengeFeedEntity
                .builder()
                .userId(challengeUser.getUserId())
                .certImgUrl(certImgUrl)
                .certContent(content)
                .roomId(challengeRoom.getId())
                .roomTitle(challengeRoom.getTitle())
                .challengeTagId(challengeRoom.getChallengeTagEntity().getTagValue())
                .build();
        challengeFeedEntityRepository.save(entity);

        fcmPushService.sendFcmPushUser(challengeRoom.getHostId(), MessageUtils.makeFcmPushRequest(MessageType.REQUEST, challengeRoom.getTitle()));
    }

    @Transactional
    public void registNoti(Integer roomId, ChallengeNotiCreateRequest challengeNotiRequest, Authentication authentication) {
        User user = UserUtils.getUserInfo(authentication);
        UserEntity entity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        ChallengeUserEntity challengeUserEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(entity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (challengeUserEntity.getRoomRole() != RoomRole.HOST) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }
        final String title = challengeNotiRequest.getTitle();
        final String content = challengeNotiRequest.getContent();

        ChallengeNotiEntity roomNotiEntity = ChallengeNotiEntity
                .builder()
                .title(title)
                .content(content)
                .build();
        roomEntity.updateNotiTitle(title);
        roomNotiEntity.addChallengeRoomEntity(roomEntity);
        challengeNotiEntityRepository.save(roomNotiEntity);
    }

    @Transactional
    public List<ChallengeNotiResponse> getNotis(Integer roomId, Authentication authentication) {
        validNotiEntity(roomId, authentication);
        return challengeNotiEntityRepository.getChallengeRoomNoti(roomId);
    }

    @Transactional
    public void updateNoti(Integer roomId, Integer notiId, ChallengeNotiUpdateRequest challengeNotiUpdateRequest, Authentication authentication) {
        validNotiEntity(roomId, authentication);
        ChallengeNotiEntity notiEntity = challengeNotiEntityRepository.findById(notiId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_NOTI));
        final String title = challengeNotiUpdateRequest.getTitle();
        final String content = challengeNotiUpdateRequest.getContent();
        if (StringUtils.hasText(title)) {
            notiEntity.updateTitle(title);
        }
        if (StringUtils.hasText(content)) {
            notiEntity.updateContent(content);
        }
        challengeNotiEntityRepository.save(notiEntity);
    }

    @Transactional
    public void deleteNoti(Integer roomId, Integer notiId, Authentication authentication) {
        validNotiEntity(roomId, authentication);
        ChallengeNotiEntity notiEntity = challengeNotiEntityRepository.findById(notiId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_NOTI));
        challengeNotiEntityRepository.delete(notiEntity);
    }


    private void validNotiEntity(Integer roomId, Authentication authentication) {
        User user = UserUtils.getUserInfo(authentication);
        UserEntity entity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        ChallengeUserEntity challengeUserEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(entity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (challengeUserEntity.getRoomRole() != RoomRole.HOST) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }
    }

    private void validValue(ValueType valueType, String value) {
        if (valueType.getCode().equals(ValueType.TAG.name())) {
            tagEntityRepository.findByTagValue(value).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_TAG));
        } else {
            categoryEntityRepository.findByCategoryValue(value).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_CATEGORY));
        }
    }


    private UserEntity getUserEntityByAuthentication(Authentication authentication) {
        User userInfo = UserUtils.getUserInfo(authentication);
        return userEntityRepository.findBySocialIdAndSocialType(userInfo.getSocialId(), userInfo.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }

    private ChallengeRoomEntity getChallengeRoomEntityById(Integer roomId) {
        return challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
    }

    private void updateBookmark(Integer roomId, Authentication authentication, String type) {

        User user = UserUtils.getUserInfo(authentication);
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        ChallengeRoomEntity challengeRoomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        ChallengeBookmarkEntity challengeBookmarkEntity = challengeBookmarkEntityRepository.findByChallengeRoomEntityAndUserEntity(challengeRoomEntity, userEntity).orElse(null);
        if (type.equals("CREATE")) {
            if (!isEmpty(challengeBookmarkEntity)) {
                throw new DodalApplicationException(ErrorCode.BOOKMARK_ALREADY_EXIST);
            }
            ChallengeBookmarkEntity saveBookmarkEntity = ChallengeBookmarkEntity
                    .builder()
                    .userEntity(userEntity)
                    .challengeRoomEntity(challengeRoomEntity)
                    .build();
            challengeBookmarkEntityRepository.save(saveBookmarkEntity);
            challengeRoomEntity.updateBookmark(1);
        } else {
            if (isEmpty(challengeBookmarkEntity)) {
                throw new DodalApplicationException(ErrorCode.NOT_FOUND_BOOKMARK);
            }
            challengeBookmarkEntityRepository.delete(challengeBookmarkEntity);
            challengeRoomEntity.updateBookmark(-1);
        }
        challengeRoomEntityRepository.save(challengeRoomEntity);
    }

    private ChallengeCreateResponse getChallengeCreateRequestFromEntities(ChallengeRoomEntity challengeRoomEntity, ChallengeTagEntity challengeTagEntity, ChallengeUserEntity challengeUserEntity) {
        return ChallengeCreateResponse.builder()
                .challengeRoomId(challengeRoomEntity.getId())
                .userId(challengeRoomEntity.getHostId())
                .nickname(challengeRoomEntity.getHostNickname())
                .title(challengeRoomEntity.getTitle())
                .content(challengeRoomEntity.getTitle())
                .thumbnailImgUrl(challengeRoomEntity.getThumbnailImgUrl())
                .recruitCnt(challengeRoomEntity.getRecruitCnt())
                .certCnt(challengeRoomEntity.getCertCnt())
                .certContent(challengeRoomEntity.getCertContent())
                .certCorrectImgUrl(challengeRoomEntity.getCertCorrectImgUrl())
                .certWrongImgUrl(challengeRoomEntity.getCertWrongImgUrl())
                .bookmarkCnt(challengeRoomEntity.getBookmarkCnt())
                .accuseCnt(challengeRoomEntity.getAccuseCnt())
                .userCnt(challengeRoomEntity.getUserCnt())
                .noticeContent(challengeRoomEntity.getNoticeTitle())
                .registeredAt(challengeRoomEntity.getRegisteredAt())
                .categoryName(challengeTagEntity.getCategoryName())
                .categoryValue(challengeTagEntity.getCategoryValue())
                .tagName(challengeTagEntity.getTagName())
                .tagValue(challengeTagEntity.getTagValue())
                .build();
    }

    private ChallengeTagEntity getChallengeTagEntity(ChallengeRoomEntity challengeRoomEntity, TagEntity tagEntity) {
        CategoryEntity categoryEntity = tagEntity.getCategoryEntity();
        return ChallengeTagEntity.builder()
                .challengeRoomEntity(challengeRoomEntity)
                .categoryName(categoryEntity.getName())
                .categoryValue(categoryEntity.getCategoryValue())
                .tagName(tagEntity.getName())
                .tagValue(tagEntity.getTagValue())
                .build();
    }



}
