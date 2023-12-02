package com.dodal.meet.repository;

import com.dodal.meet.model.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagEntityRepository extends JpaRepository<TagEntity, Integer> {
    Optional<TagEntity> findByTagValue(String value);


    @Query("SELECT t FROM TagEntity t WHERE t.tagValue in :tagValueList")
    List<TagEntity> findAllByTagValueList(@Param("tagValueList") List<String> tagValueList);
}
