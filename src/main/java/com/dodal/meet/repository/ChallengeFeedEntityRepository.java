package com.dodal.meet.repository;

import com.dodal.meet.model.entity.ChallengeFeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeFeedEntityRepository extends JpaRepository<ChallengeFeedEntity, Long> {
}
