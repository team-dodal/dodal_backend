package com.dodal.meet.repository;

import com.dodal.meet.model.entity.ChallengeBookmarkEntity;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ChallengeBookmarkEntityRepository extends JpaRepository<ChallengeBookmarkEntity, Long> {

    Optional<ChallengeBookmarkEntity> findByChallengeRoomEntityAndUserEntity(ChallengeRoomEntity challengeRoomEntity, UserEntity userEntity);
}
