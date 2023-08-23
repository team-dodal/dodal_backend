package com.dodal.meet.repository;

import com.dodal.meet.model.entity.FeedLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedLikeEntityRepository extends JpaRepository<FeedLikeEntity, Long> {

    @Query("SELECT f FROM FeedLikeEntity f WHERE f.challengeFeedEntity.id = :feedId AND f.likeUserId = :userId")
    Optional<FeedLikeEntity> findByFeedInfo(@Param("feedId")Long feedId, @Param("userId")Long userId);
}
