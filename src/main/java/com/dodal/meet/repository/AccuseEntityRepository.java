package com.dodal.meet.repository;

import com.dodal.meet.model.entity.AccuseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccuseEntityRepository extends JpaRepository<AccuseEntity, Long> {

    AccuseEntity findBySourceUserIdAndTargetUserId(Long id, Long targetUserId);
}
