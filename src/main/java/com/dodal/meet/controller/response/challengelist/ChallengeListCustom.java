package com.dodal.meet.controller.response.challengelist;

import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChallengeListCustom {

    Page<ChallengeUserRoleResponse> getChallengeUser(Pageable pageable, UserEntity userEntity);
    Page<ChallengeUserRoleResponse> getChallengeHost(Pageable pageable, UserEntity userEntity);

}
