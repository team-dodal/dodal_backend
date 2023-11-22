package com.dodal.meet.service;

import com.dodal.meet.controller.request.fcm.FcmKafkaPush;
import com.dodal.meet.controller.response.challengemanage.ChallengeCertImgManage;
import com.dodal.meet.controller.response.challengemanage.ChallengeHostRoleResponse;
import com.dodal.meet.controller.response.challengemanage.ChallengeUserInfoResponse;
import com.dodal.meet.controller.response.challengemanage.ChallengeUserRoleResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.ChallengeFeedEntity;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.ChallengeUserEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.producer.PushProducer;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeListService {

    private final ChallengeFeedEntityRepository challengeFeedEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;
    private final ChallengeUserEntityRepository challengeUserEntityRepository;
    private final FcmPushService fcmPushService;

    private final AlarmService alarmService;

    private final PushProducer pushProducer;

    @Transactional
    public List<ChallengeUserRoleResponse> getUserRoleChallengeRooms(final Authentication authentication) {
        UserEntity userEntity = getUserEntity(authentication);
        return challengeRoomEntityRepository.getChallengeUser(userEntity);
    }

    @Transactional
    public List<ChallengeHostRoleResponse> getHostRoleChallengeRooms(final Authentication authentication) {
        UserEntity userEntity = getUserEntity(authentication);
        return challengeRoomEntityRepository.getChallengeHost(userEntity);
    }

    @Transactional
    public void updateFeedStatus(final Integer roomId, final Long feedId, final String confirmYN, final Authentication authentication) {
        final UserEntity userEntity = getUserEntity(authentication);
        final ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        final ChallengeUserEntity challengeHostEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (!challengeHostEntity.getRoomRole().equals(RoomRole.HOST)) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));

        StringUtils.equalsAny(confirmYN, DtoUtils.Y, DtoUtils.N);
        if(StringUtils.equals(confirmYN, DtoUtils.Y)){
            feedEntity.updateCertCode(FeedUtils.CONFIRM);
            final ChallengeUserEntity challengeUserEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(feedEntity.getUserId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
            final int curContinueCertCnt = challengeHostEntity.getContinueCertCnt();
            feedEntity.updateContinueCertCnt(curContinueCertCnt + DtoUtils.ONE);

            // 도전방 유저 정보 업데이트 - (전체 인증 횟수, 연속 인증 횟수, 최대 연속 인증 횟수)
            challengeUserEntity.updateCertCnts(DtoUtils.ONE);
            // Feed를 올린 사용자에게 알림 이력 및 FCM 푸시 알림
            alarmService.saveAlarmHist(MessageUtils.makeAlarmHistResponse(MessageType.CONFIRM, roomEntity.getTitle(), feedEntity.getUserId(), roomId));
            pushProducer.send(FcmKafkaPush.makeKafkaPush(feedEntity.getUserId(), MessageUtils.makeFcmPushRequest(MessageType.CONFIRM, roomEntity.getTitle())));

        } else if (StringUtils.equals(confirmYN, DtoUtils.N)) {
            feedEntity.updateCertCode(FeedUtils.REJECT);
            // Feed를 올린 사용자에게 알림 이력 및 FCM 푸시 알림
            alarmService.saveAlarmHist(MessageUtils.makeAlarmHistResponse(MessageType.REJECT, roomEntity.getTitle(), feedEntity.getUserId(), roomId));
            pushProducer.send(FcmKafkaPush.makeKafkaPush(feedEntity.getUserId(), MessageUtils.makeFcmPushRequest(MessageType.REJECT, roomEntity.getTitle())));
        } else {
            throw new DodalApplicationException(ErrorCode.INVALID_YN_REQUEST);
        }
    }

    @Transactional
    public Map<String, List<ChallengeCertImgManage>> getCertImgList(final Integer roomId, final String dateYM, final Authentication authentication) {
        DateUtils.validDateYM(dateYM);
        UserEntity userEntity = getUserEntity(authentication);
        ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        ChallengeUserEntity challengeUserEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (!challengeUserEntity.getRoomRole().equals(RoomRole.HOST)) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }

        List<ChallengeCertImgManage> certImgList = challengeRoomEntityRepository.getCertImgList(roomId, dateYM);

        Map<String, List<ChallengeCertImgManage>> response = new LinkedHashMap<>();
        for (ChallengeCertImgManage certImg : certImgList) {
            final String dayOfWeekDetail = DateUtils.convertToDayOfWeekDetail(certImg.getRegisteredDate());

            if (ObjectUtils.isEmpty(response.get(dayOfWeekDetail))) {
                List<ChallengeCertImgManage> certList = new ArrayList<>();
                certList.add(certImg);
                response.put(dayOfWeekDetail, certList);
            } else {
                response.get(dayOfWeekDetail).add(certImg);
            }
        }
        return response;
    }

    private UserEntity getUserEntity(Authentication authentication) {
        User user = UserUtils.getUserInfo(authentication);
        return userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
    }

    @Transactional(readOnly = true)
    public List<ChallengeUserInfoResponse> getUserList(Integer roomId, User user) {
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        final ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        final ChallengeUserEntity challengeHostEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (!challengeHostEntity.getRoomRole().equals(RoomRole.HOST)) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }

        Map<Integer, String> weekInfo = DateUtils.getWeekInfo();
        return challengeRoomEntityRepository.getUserList(roomId, weekInfo.get(DateUtils.MON), weekInfo.get(DateUtils.SUN));
    }

    @Transactional
    public void deleteChallengeUser(User user, Integer roomId, Long userId) {
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        if (userEntity.getId() == userId) {
            throw new DodalApplicationException(ErrorCode.INVALID_USER_KICK_OUT);
        }
        final ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        final ChallengeUserEntity challengeHostEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (!challengeHostEntity.getRoomRole().equals(RoomRole.HOST)) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }

        fcmPushService.sendFcmPushUser(userId, MessageUtils.makeFcmPushRequest(MessageType.KICK_OUT, roomEntity.getTitle()));
        roomEntity.updateUserCnt(DtoUtils.MINUS_ONE);
        challengeUserEntityRepository.deleteByUserId(userId);
    }

    @Transactional
    public void changeHost(User user, Integer roomId, Long userId) {
        UserEntity hostEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        UserEntity targetEntity = userEntityRepository.findById(userId).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        ChallengeUserEntity host = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(hostEntity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        ChallengeUserEntity target = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(targetEntity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (!host.getRoomRole().equals(RoomRole.HOST)) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }

        host.updateRole(RoomRole.USER);
        target.updateRole(RoomRole.HOST);

        roomEntity.updateUserInfo(targetEntity);
        fcmPushService.sendFcmPushUser(targetEntity.getId(), MessageUtils.makeFcmPushRequest(MessageType.MANDATE, roomEntity.getTitle()));
    }
}
