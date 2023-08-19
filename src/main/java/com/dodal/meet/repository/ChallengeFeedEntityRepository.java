package com.dodal.meet.repository;

import com.dodal.meet.model.entity.ChallengeFeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeFeedEntityRepository extends JpaRepository<ChallengeFeedEntity, Long> {

    List<ChallengeFeedEntity> findAllByUserIdAndRegisteredDate(Long userId, String registeredDate);
}
