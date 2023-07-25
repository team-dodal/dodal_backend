package com.dodal.meet.service;

import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCreateRequest;
import com.dodal.meet.controller.response.challenge.ChallengeCreateResponse;
import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCondition;
import com.dodal.meet.controller.response.challenge.ChallengeRoomDetailResponse;
import com.dodal.meet.controller.response.challenge.ChallengeRoomSearchResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.RoomSearchType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.util.ObjectUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeRoomService {
    private final ChallengeBookmarkEntityRepository challengeBookmarkEntityRepository;
    private final UserEntityRepository userEntityRepository;

    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;
    private final ChallengeUserEntityRepository challengeUserEntityRepository;
    private final ChallengeTagEntityRepository challengeTagEntityRepository;
    private final TagEntityRepository tagEntityRepository;

    private final UserService userService;
    private final ImageService imageService;



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
        challengeRoomEntity.updateImgUrl(thumbnailImgUrl, certCorrectImgUrl, certWrongImgUrl);
        challengeRoomEntityRepository.save(challengeRoomEntity);

        UserEntity userEntity = userService.userToUserEntity(authentication);
        ChallengeUserEntity challengeUserEntity = ChallengeUserEntity.getHostEntity(userEntity);

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
            tagEntityRepository.findByTagValue(tagValue).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_TAG));
        }
        ChallengeRoomCondition challengeRoomCondition = ChallengeRoomCondition
                .builder()
                .roomSearchType(RoomSearchType.of(condition.toUpperCase()))
                .tagValue(tagValue)
                .build();

        return challengeRoomEntityRepository.getChallengeRooms(challengeRoomCondition, pageable, userEntity);
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
                .userId(challengeUserEntity.getUserId())
                .nickname(challengeUserEntity.getNickname())
                .title(challengeRoomEntity.getTitle())
                .content(challengeRoomEntity.getTitle())
                .thumbnailImgUrl(challengeRoomEntity.getThumbnailImgUrl())
                .recruitCnt(challengeRoomEntity.getRecruitCnt())
                .certCnt(challengeRoomEntity.getCertCnt())
                .certContent(challengeRoomEntity.getCertContent())
                .certCorrectImgUrl(challengeRoomEntity.getCertCorrectImgUrl())
                .certWrongImgUrl(challengeRoomEntity.getCertWrongImgUrl())
                .bookmarkCnt(challengeRoomEntity.getBookmarkCnt())
                .warnContent(challengeRoomEntity.getWarnContent())
                .accuseCnt(challengeRoomEntity.getAccuseCnt())
                .userCnt(challengeRoomEntity.getUserCnt())
                .noticeContent(challengeRoomEntity.getNoticeContent())
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
