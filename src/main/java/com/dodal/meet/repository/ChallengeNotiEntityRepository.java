package com.dodal.meet.repository;

import com.dodal.meet.model.entity.ChallengeNotiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeNotiEntityRepository extends JpaRepository<ChallengeNotiEntity, Integer> {
}
