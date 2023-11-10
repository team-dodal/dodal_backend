package com.dodal.meet.repository;

import com.dodal.meet.controller.response.feed.FeedCustom;
import com.dodal.meet.model.entity.ChallengeFeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeFeedEntityRepository extends JpaRepository<ChallengeFeedEntity, Long>, FeedCustom {

    List<ChallengeFeedEntity> findAllByUserIdAndRoomIdAndRegisteredDate(Long userId, Integer roomId, String registeredDate);
}
