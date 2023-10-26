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

    @Query(
            "SELECT u " +
            "FROM ChallengeUserEntity u " +
            "WHERE u.userEntity.id = :userId AND u.challengeRoomEntity = :challengeRoomEntity"
    )
    Optional<ChallengeUserEntity> findByUserIdAndChallengeRoomEntity(@Param("userId") Long userId, @Param("challengeRoomEntity") ChallengeRoomEntity challengeRoomEntity);


    @Query(
            "SELECT u " +
            "FROM ChallengeUserEntity u " +
            "WHERE u.userEntity.id = :userId"
    )
    List<ChallengeUserEntity> findAllByUserId(@Param("userId") Long userId);

    @Query(
            "SELECT new com.dodal.meet.controller.response.user.UserRoomCertInfo(max(u.maxContinueCertCnt), cast(sum(u.totalCertCnt) as int )) " +
            "FROM ChallengeUserEntity u " +
            "WHERE u.userEntity.id = :userId"
    )
    UserRoomCertInfo findMaxCertInfoByUserId(@Param("userId") Long userId);

    @Query(
            "DELETE " +
            "FROM ChallengeUserEntity u " +
            "WHERE u.userEntity.id = :userId"
    )
    void deleteByUserId(@Param("userId") Long userId);
}
