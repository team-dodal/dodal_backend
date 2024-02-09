package com.dodal.meet.repository;

import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    @Query(
        "SELECT u " +
        "FROM UserEntity u " +
        "WHERE u.id = :userId " +
        "AND u.socialType = :socialType"
    )
    Optional<UserEntity> findByUserIdAndSocialType(@Param("userId") Long userId, @Param("socialType") SocialType socialType);
    Optional<UserEntity> findByNickname(String nickname);

    Optional<UserEntity> findBySocialIdAndSocialType(String socialId, SocialType socialType);
}
