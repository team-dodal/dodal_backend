package com.dodal.meet.repository;

import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.ChallengeUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeUserEntityRepository extends JpaRepository<ChallengeUserEntity, Integer> {

    Optional<ChallengeUserEntity> findByUserIdAndChallengeRoomEntity(Long userId, ChallengeRoomEntity challengeRoomEntity);

    Optional<ChallengeUserEntity> findByChallengeRoomEntityAndRoomRole(ChallengeRoomEntity challengeRoom, RoomRole roomRole);
}
