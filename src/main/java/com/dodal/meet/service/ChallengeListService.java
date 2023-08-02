package com.dodal.meet.service;

import com.dodal.meet.controller.response.Response;
import com.dodal.meet.controller.response.challengelist.ChallengeUserRoleResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeListService {

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
    private final ChallengeRoomService challengeRoomService;


    @Transactional
    public Page<ChallengeUserRoleResponse> getUserRoleChallengeRooms(Authentication authentication) {
        User user = UserUtils.getUserInfo(authentication);
        UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        Pageable pageable = PageRequest.of(0, 3);
        return challengeRoomEntityRepository.getChallengeUser(pageable, userEntity);
    }
}
