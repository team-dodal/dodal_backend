package com.dodal.meet.repository;

import com.dodal.meet.model.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TagEntityRepository extends JpaRepository<TagEntity, Integer> {
    Optional<TagEntity> findByValue(String value);
}
