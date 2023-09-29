package com.dodal.meet.repository;

import com.dodal.meet.model.entity.ChallengeWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeWordEntityRepository extends JpaRepository<ChallengeWordEntity, Long> {

    @Query(
            "SELECT  w.word " +
            "FROM ChallengeWordEntity w " +
            "WHERE w.userId = :userId " +
            "ORDER BY w.registeredAt DESC "
    )
    List<String> findWordsByUserIdAndOrderedDesc(@Param("userId") Long userId);

    @Modifying
    @Query(
            "DELETE " +
            "FROM ChallengeWordEntity w " +
            "WHERE w.userId = :userId"
    )
    void deleteAllByUserId(@Param("userId") Long userId);
}
