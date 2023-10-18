package com.dodal.meet.repository;

import com.dodal.meet.controller.response.user.UserRoomCertInfo;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.ChallengeUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeUserEntityRepository extends JpaRepository<ChallengeUserEntity, Integer> {

    Optional<ChallengeUserEntity> findByUserIdAndChallengeRoomEntity(Long userId, ChallengeRoomEntity challengeRoomEntity);

    List<ChallengeUserEntity> findAllByUserId(Long userId);

    @Query(
            "SELECT new com.dodal.meet.controller.response.user.UserRoomCertInfo(max(u.maxContinueCertCnt), cast(sum(u.totalCertCnt) as int )) " +
            "FROM ChallengeUserEntity u " +
            "WHERE u.userId = :userId"
    )
    UserRoomCertInfo findMaxCertInfoByUserId(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}
