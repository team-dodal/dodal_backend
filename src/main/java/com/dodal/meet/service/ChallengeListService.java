package com.dodal.meet.service;

import com.dodal.meet.controller.response.challengemanage.ChallengeCertImgManage;
import com.dodal.meet.controller.response.challengemanage.ChallengeHostRoleResponse;
import com.dodal.meet.controller.response.challengemanage.ChallengeUserRoleResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.ChallengeFeedEntity;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.ChallengeUserEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
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
        final ChallengeUserEntity challengeUserEntity = challengeUserEntityRepository.findByUserIdAndChallengeRoomEntity(userEntity.getId(), roomEntity).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM_USER));
        if (!challengeUserEntity.getRoomRole().equals(RoomRole.HOST)) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
        }
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));

        StringUtils.equalsAny(confirmYN, DtoUtils.Y, DtoUtils.N);
        if(StringUtils.equals(confirmYN, DtoUtils.Y)){
            feedEntity.updateCertCode(FeedUtils.CONFIRM);
            fcmPushService.sendFcmPushUser(feedEntity.getUserId(), MessageUtils.makeFcmPushRequest(MessageType.CONFIRM, roomEntity.getTitle()));
        } else if (StringUtils.equals(confirmYN, DtoUtils.N)) {
            feedEntity.updateCertCode(FeedUtils.REJECT);
            fcmPushService.sendFcmPushUser(feedEntity.getUserId(), MessageUtils.makeFcmPushRequest(MessageType.REJECT, roomEntity.getTitle()));
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


}