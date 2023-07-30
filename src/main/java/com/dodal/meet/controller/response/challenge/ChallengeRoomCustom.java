package com.dodal.meet.controller.response.challenge;

import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCondition;
import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomSearchCategoryRequest;
import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChallengeRoomCustom {
    Page<ChallengeRoomSearchResponse> getChallengeRooms(ChallengeRoomCondition challengeRoomCondition, Pageable pageable, UserEntity userEntity);
    Page<ChallengeRoomSearchResponse> getChallengeRoomsByCategory(ChallengeRoomSearchCategoryRequest request, Pageable pageable, UserEntity userEntity);
    ChallengeRoomDetailResponse getChallengeRoomDetail(Integer roomId, UserEntity userEntity);

    List<ChallengeNotiResponse> getChallengeRoomNoti(Integer roomId);
}
