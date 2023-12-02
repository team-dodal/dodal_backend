package com.dodal.meet.service;

import com.dodal.meet.controller.request.challengeroom.*;
import com.dodal.meet.controller.request.fcm.FcmKafkaPush;
import com.dodal.meet.controller.response.challengeroom.*;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.RoomSearchType;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.producer.PushProducer;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeRoomService {
    private final ChallengeWordEntityRepository challengeWordEntityRepository;
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

    private final PushProducer pushProducer;
    private final AlarmService alarmService;


    @Transactional
    public ChallengeCreateResponse createChallengeRoom(final ChallengeRoomCreateRequest request, final User user) {

        ChallengeRoomEntity challengeRoomEntity = ChallengeRoomEntity.newInstance(request);

        final String tagValue = request.getTagValue();
        final TagEntity tagEntity = tagEntityRepository.findByTagValue(tagValue).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_TAG));
        final String categoryValue = tagEntity.getCategoryEntity().getCategoryValue();

        // 요청 온 이미지가 없는 경우 카테고리 값을 비교하여 서버에 저장된 디폴트 이미지를 저장한다.
        if (!StringUtils.hasText(request.getThumbnailImgUrl())) {
            challengeRoomEntity.updateDefaultImgUrl(ImageUtils.findByCategoryValueToRoomThumbnailImageUrl(categoryValue));
        }

        UserEntity userEntity = userService.userToUserEntity(user);
        ChallengeUserEntity challengeUserEntity = ChallengeUserEntity.fromHostEntity(userEntity);

        challengeRoomEntity.updateUserInfo(userEntity);
        challengeRoomEntityRepository.save(challengeRoomEntity);

        challengeUserEntity.addChallengeRoomEntity(challengeRoomEntity);
        challengeUserEntityRepository.save(challengeUserEntity);


        ChallengeTagEntity challengeTagEntity = getChallengeTagEntity(challengeRoomEntity, tagEntity);
        challengeRoomEntity.addChallengeTagEntity(challengeTagEntity);
        challengeTagEntityRepository.save(challengeTagEntity);

        final ChallengeRoomDetailResponse challengeRoomDetail = challengeRoomEntityRepository.getChallengeRoomDetail(challengeRoomEntity.getId(), userEntity);

        return ChallengeCreateResponse.newInstance(challengeRoomDetail);
    }

    @Transactional
    public Page<ChallengeRoomSearchResponse> getChallengeRooms(final String condition, final String categoryValue, final Pageable pageable, final User user) {
        UserEntity userEntity = userService.userToUserEntity(user);
        if (!isEmpty(categoryValue)) {
            validValue(ValueType.CATEGORY, categoryValue);
        }
        ChallengeRoomCondition challengeRoomCondition = ChallengeRoomCondition
                .builder()
                .roomSearchType(RoomSearchType.of(condition))
                .categoryValue(categoryValue)
                .build();

        return challengeRoomEntityRepository.getChallengeRooms(challengeRoomCondition, pageable, userEntity);
    }

    @Transactional(readOnly = true)
    public Page<ChallengeRoomSearchResponse> getChallengeRoomsByCategory(final ChallengeRoomSearchCategoryRequest request, final Pageable pageable, final User user) {
        UserEntity userEntity = userService.userToUserEntity(user);
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
    public void createBookmark(final Integer roomId, final User user) {
        updateBookmark(roomId, user, "CREATE");
    }

    @Transactional
    public void deleteBookmark(final Integer roomId, final User user) {
        updateBookmark(roomId, user, "DELETE");
    }

    @Transactional(readOnly = true)
    public ChallengeRoomDetailResponse getChallengeRoomDetail(final Integer roomId, final User user) {
        UserEntity userEntity = userService.userToUserEntity(user);
        getChallengeRoomEntityById(roomId);
        return challengeRoomEntityRepository.getChallengeRoomDetail(roomId,userEntity);
    }

    @Transactional
    public ChallengeRoomDetailResponse joinChallengeRoom(final Integer roomId, final User user) {

        final ChallengeRoomEntity challengeRoom = getChallengeRoomEntityById(roomId);
        final UserEntity userEntity = userService.userToUserEntity(user);
        ChallengeUserEntity challengeUser = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), challengeRoom).orElse(null);
        // 이미 가입된 회원이라면
        if (!isEmpty(challengeUser)) {
            throw new DodalApplicationException(ErrorCode.INVALID_ROOM_JOIN);
        }
        ChallengeUserEntity challengeUserEntity = ChallengeUserEntity.builder()
                .challengeRoomEntity(challengeRoom)
                .roomRole(RoomRole.USER)
                .continueCertCnt(0)
                .userEntity(userEntity)
                .build();
        challengeUserEntityRepository.save(challengeUserEntity);
        challengeRoom.updateUserCnt(1);
        challengeRoomEntityRepository.save(challengeRoom);

        return challengeRoomEntityRepository.getChallengeRoomDetail(roomId, userEntity);

    }

    @Transactional
    public void leaveChallengeRoom(final Integer roomId, final User user) {
        final ChallengeRoomEntity challengeRoom = getChallengeRoomEntityById(roomId);
        final UserEntity userEntity = userService.userToUserEntity(user);
        ChallengeUserEntity challengeUser = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), challengeRoom)
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_ROOM_LEAVE));
        challengeUserEntityRepository.delete(challengeUser);
        challengeRoom.updateUserCnt(-1);
        challengeRoomEntityRepository.save(challengeRoom);
    }

    @Transactional
    public void createCertification(final Integer roomId, final ChallengeFeedCreateRequest request, final User user) {
        final ChallengeRoomEntity challengeRoom = getChallengeRoomEntityById(roomId);

        UserEntity userEntity = userService.userToUserEntity(user);
        ChallengeUserEntity challengeUser = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), challengeRoom).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        String today = DateUtils.parsingTimestamp(Timestamp.from(Instant.now()));
        List<ChallengeFeedEntity> userTodayFeedList = challengeFeedEntityRepository.findAllByUserIdAndRoomIdAndRegisteredDate(userEntity.getId(), roomId, today);
        if (!isEmpty(userTodayFeedList)) {
            for (ChallengeFeedEntity entity : userTodayFeedList) {
                if (entity.getCertCode().equals(FeedUtils.REQUEST) || entity.getCertCode().equals(FeedUtils.CONFIRM)) {
                    throw new DodalApplicationException(ErrorCode.FEED_ALREADY_REQUEST);
                }
            }
        }
        ChallengeFeedEntity entity = ChallengeFeedEntity.newInstance(request, challengeRoom, challengeUser);

        challengeFeedEntityRepository.save(entity);

        alarmService.saveAlarmHist(MessageUtils.makeAlarmHistResponse(MessageType.REQUEST, challengeRoom.getTitle(), challengeRoom.getHostId(), roomId));

        pushProducer.send(FcmKafkaPush.makeKafkaPush(challengeRoom.getHostId(), MessageUtils.makeFcmPushRequest(MessageType.REQUEST, challengeRoom.getTitle())));
    }



    @Transactional
    public void registNoti(final Integer roomId, final ChallengeNotiCreateRequest challengeNotiRequest, final User user) {
        UserEntity entity = userService.userToUserEntity(user);
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
    public List<ChallengeNotiResponse> getNotis(final Integer roomId, final User user) {
        validNotiEntity(roomId, user);
        return challengeNotiEntityRepository.getChallengeRoomNoti(roomId);
    }

    @Transactional
    public void updateNoti(final Integer roomId, final Integer notiId, final ChallengeNotiUpdateRequest challengeNotiUpdateRequest, final User user) {
        validNotiEntity(roomId, user);
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
    public void deleteNoti(final Integer roomId, final Integer notiId, final User user) {
        validNotiEntity(roomId, user);
        ChallengeNotiEntity notiEntity = challengeNotiEntityRepository.findById(notiId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_NOTI));
        challengeNotiEntityRepository.delete(notiEntity);
    }

    @Transactional
    public ChallengeRoomDetailResponse updateChallengeRoom(final Integer roomId, final ChallengeRoomUpdateRequest challengeRoomUpdateRequest, final User user) {
        final UserEntity entity = userService.userToUserEntity(user);
        ChallengeRoomEntity challengeRoomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        TagEntity tagEntity = tagEntityRepository.findByTagValue(challengeRoomUpdateRequest.getTagValue()).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_TAG));

        final ChallengeUserEntity challengeUserEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(entity.getId(), challengeRoomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (!challengeUserEntity.getRoomRole().equals(RoomRole.HOST)) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }

        // 이미지 수정은 값이 있을 경우 기존 이미지를 S3에서 제거한다.
        final String thumbnailImgUrl = challengeRoomUpdateRequest.getThumbnailImgUrl();
        final String certCorrectImgUrl = challengeRoomUpdateRequest.getCertCorrectImgUrl();
        final String certWrongImgUrl = challengeRoomUpdateRequest.getCertWrongImgUrl();

        compareBeforeAndAfterImgUrl(challengeRoomEntity.getThumbnailImgUrl(), thumbnailImgUrl);
        compareBeforeAndAfterImgUrl(challengeRoomEntity.getCertCorrectImgUrl(), certCorrectImgUrl);
        compareBeforeAndAfterImgUrl(challengeRoomEntity.getCertWrongImgUrl(), certWrongImgUrl);

        challengeRoomEntity.updateChallengeRoom(challengeRoomUpdateRequest);

        if (challengeRoomUpdateRequest.getTagValue() != challengeRoomEntity.getChallengeTagEntity().getTagValue()) {
            ChallengeTagEntity challengeTagEntity = getChallengeTagEntity(challengeRoomEntity, tagEntity);
            challengeRoomEntity.addChallengeTagEntity(challengeTagEntity);
        }

        challengeRoomEntityRepository.save(challengeRoomEntity);

        return getChallengeRoomDetail(roomId, user);
    }

    private void compareBeforeAndAfterImgUrl(final String before, final String after) {
        if (before != after && StringUtils.hasText(before) && before.indexOf("s3") != -1) {
            removeBeforeImg(before);
        }
    }

    private void removeBeforeImg(String before) {
        imageService.deleteImg(before);
    }


    private void validNotiEntity(final Integer roomId, final User user) {
        UserEntity entity = userService.userToUserEntity(user);
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


    private ChallengeRoomEntity getChallengeRoomEntityById(final Integer roomId) {
        return challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
    }

    private void updateBookmark(final Integer roomId, final User user, final String type) {
        UserEntity userEntity = userService.userToUserEntity(user);
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

    @Transactional(readOnly = true)
    public List<ChallengeRoomRankResponse> getRank(Integer roomId, String code, User user) {
        userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));

        // 전체 조회
        if (code.equals("0")) {
            return challengeRoomEntityRepository.getRankAll(roomId);
        } else if (code.equals("1")) {
            String month = DateUtils.getMonth();
            return challengeRoomEntityRepository.getRankMonth(roomId, month);
        } else {
            Map<Integer, String> weekInfo = DateUtils.getWeekInfo();
            String startDay = weekInfo.get(DateUtils.MON);
            String endDay = weekInfo.get(DateUtils.SUN);
            return challengeRoomEntityRepository.getRankWeek(roomId, startDay, endDay);
        }
    }


    @Transactional
    public Page<ChallengeRoomSearchResponse> getChallengeRoomsByWord(User user, ChallengeRoomSearchRequest request) {
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        ChallengeWordEntity wordEntity = ChallengeWordEntity.builder()
                .word(request.getWord())
                .userId(userEntity.getId())
                .build();
        challengeWordEntityRepository.save(wordEntity);
        return challengeRoomEntityRepository.getChallengeRoomsByWord(userEntity, request);
    }

    @Transactional(readOnly = true)
    public List<String> getChallengeWordsByUserId(User user) {
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        List<String> words = challengeWordEntityRepository.findWordsByUserIdAndOrderedDesc(userEntity.getId());
        return words.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteChallengeWordsByUserId(User user) {
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        challengeWordEntityRepository.deleteAllByUserId(userEntity.getId());
    }

    @Transactional
    public void updateChallengeUserCertCnt() {
        challengeRoomEntityRepository.updateChallengeUserCertCnt();
    }

    @Transactional(readOnly = true)
    public List<ChallengeRoomBookmarkResponse> getBookmarksByUser(User user) {
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(()-> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        return challengeRoomEntityRepository.getBookmarksByUser(userEntity);
    }
}
