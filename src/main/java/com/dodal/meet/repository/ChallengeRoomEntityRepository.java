package com.dodal.meet.repository;

import com.dodal.meet.controller.request.challengeroom.ChallengeRoomCondition;
import com.dodal.meet.controller.response.challengemanage.ChallengeManageCustom;
import com.dodal.meet.controller.response.challengemanage.ChallengeUserRoleResponse;
import com.dodal.meet.controller.response.challengeroom.ChallengeRoomCustom;
import com.dodal.meet.controller.response.challengeroom.ChallengeRoomRankResponse;
import com.dodal.meet.controller.response.challengeroom.ChallengeRoomSearchResponse;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRoomEntityRepository extends JpaRepository<ChallengeRoomEntity, Integer>, ChallengeRoomCustom, ChallengeManageCustom {

    List<ChallengeRoomEntity> findAllByHostId(Long hostId);

    @Override
    Page<ChallengeRoomSearchResponse> getChallengeRooms(ChallengeRoomCondition challengeRoomCondition, Pageable pageable, UserEntity userEntity);

    @Override
    List<ChallengeUserRoleResponse> getChallengeUser(UserEntity userEntity);

    @Query(
            "SELECT new com.dodal.meet.controller.response.challengeroom.ChallengeRoomRankResponse(u.userEntity.id, u.userEntity.nickname, ue.profileUrl, count(*)) " +
            "FROM ChallengeRoomEntity r " +
                "INNER JOIN ChallengeUserEntity u ON r.id = u.challengeRoomEntity.id " +
                "INNER JOIN ChallengeFeedEntity f ON (f.roomId = r.id and u.userEntity.id = f.userId) " +
                "INNER JOIN UserEntity ue ON u.userEntity.id = ue.id " +
            "WHERE r.id = :roomId AND f.certCode = '2' " +
            "GROUP BY u.userEntity.id, u.userEntity.nickname, ue.profileUrl " +
            "ORDER BY count(*) DESC"
    )
    List<ChallengeRoomRankResponse> getRankAll(@Param("roomId") Integer roomId);

    @Query(
            "SELECT new com.dodal.meet.controller.response.challengeroom.ChallengeRoomRankResponse(u.userEntity.id, u.userEntity.nickname, ue.profileUrl, count(*)) " +
            "FROM ChallengeRoomEntity r " +
                "INNER JOIN ChallengeUserEntity u ON r.id = u.challengeRoomEntity.id " +
                "INNER JOIN ChallengeFeedEntity f ON (f.roomId = r.id AND u.userEntity.id = f.userId) " +
                "INNER JOIN UserEntity ue ON u.userEntity.id = ue.id " +
            "WHERE r.id = :roomId AND f.certCode = '2' AND f.registeredDate BETWEEN :startDay AND :endDay " +
            "GROUP BY u.userEntity.id, u.userEntity.nickname, ue.profileUrl " +
            "ORDER BY count(*) DESC"
    )
    List<ChallengeRoomRankResponse> getRankWeek(@Param("roomId") Integer roomId, @Param("startDay") String startDay, @Param("endDay") String endDay);

    @Query(
            "SELECT new com.dodal.meet.controller.response.challengeroom.ChallengeRoomRankResponse(u.userEntity.id, u.userEntity.nickname, ue.profileUrl, count(*)) " +
            "FROM ChallengeRoomEntity r " +
                "INNER JOIN ChallengeUserEntity u ON r.id = u.challengeRoomEntity.id " +
                "INNER JOIN ChallengeFeedEntity f ON (f.roomId = r.id AND u.userEntity.id = f.userId) " +
                "INNER JOIN UserEntity ue ON u.userEntity.id = ue.id " +
            "WHERE r.id = :roomId AND f.certCode = '2' AND substring(f.registeredDate, 1, 6) = :month " +
            "GROUP BY u.userEntity.id, u.userEntity.nickname, ue.profileUrl " +
            "ORDER BY count(*) DESC"
    )
    List<ChallengeRoomRankResponse> getRankMonth(@Param("roomId") Integer roomId, @Param("month") String month);

    @Modifying
    @Query(
            "UPDATE ChallengeRoomEntity r " +
            "SET r.hostNickname = :nickname, r.hostProfileUrl = :profileUrl " +
            "WHERE r.hostId = :userId"
    )
    void updateNicknameAndProfileUrlByHostUserId(@Param("userId") Long userId, @Param("nickname") String nickname, @Param("profileUrl") String profileUrl);
}
