package com.dodal.meet.controller.response.challengelist;

import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChallengeListCustom {

    List<ChallengeUserRoleResponse> getChallengeUser(UserEntity userEntity);
    List<ChallengeHostRoleResponse> getChallengeHost(UserEntity userEntity);

}
