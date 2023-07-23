package com.dodal.meet.repository;

import com.dodal.meet.model.entity.ChallengeUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeUserEntityRepository extends JpaRepository<ChallengeUserEntity, Integer> {

}
