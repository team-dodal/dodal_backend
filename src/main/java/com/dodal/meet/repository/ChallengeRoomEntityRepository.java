package com.dodal.meet.repository;

import com.dodal.meet.model.entity.ChallengeRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRoomEntityRepository extends JpaRepository<ChallengeRoomEntity, Integer> {
}
