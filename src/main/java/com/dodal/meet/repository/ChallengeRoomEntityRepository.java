package com.dodal.meet.repository;

import com.dodal.meet.controller.request.challengeroom.ChallengeRoomCondition;
import com.dodal.meet.controller.response.challengeroom.ChallengeRoomCustom;
import com.dodal.meet.controller.response.challengeroom.ChallengeRoomSearchResponse;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRoomEntityRepository extends JpaRepository<ChallengeRoomEntity, Integer>, ChallengeRoomCustom {
    @Override
    Page<ChallengeRoomSearchResponse> getChallengeRooms(ChallengeRoomCondition challengeRoomCondition, Pageable pageable, UserEntity userEntity);
}
