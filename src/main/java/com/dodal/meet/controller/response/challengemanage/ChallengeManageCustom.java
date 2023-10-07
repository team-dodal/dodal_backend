package com.dodal.meet.controller.response.challengemanage;

import com.dodal.meet.model.entity.UserEntity;

import java.util.List;

public interface ChallengeManageCustom {

    List<ChallengeUserRoleResponse> getChallengeUser(final UserEntity userEntity);
    List<ChallengeHostRoleResponse> getChallengeHost(final UserEntity userEntity);

    List<ChallengeCertImgManage> getCertImgList(final Integer roomId, final String dateYM);

    List<ChallengeUserInfoResponse> getUserList(Integer roomId, String startDate, String endDate);
}
