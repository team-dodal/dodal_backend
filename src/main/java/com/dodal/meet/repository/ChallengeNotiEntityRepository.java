package com.dodal.meet.repository;

import com.dodal.meet.controller.response.challenge.ChallengeNotiResponse;
import com.dodal.meet.controller.response.challenge.ChallengeRoomCustom;
import com.dodal.meet.model.entity.ChallengeNotiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeNotiEntityRepository extends JpaRepository<ChallengeNotiEntity, Integer>, ChallengeRoomCustom {
    @Override
    List<ChallengeNotiResponse> getChallengeRoomNoti(Integer roomId);
}
