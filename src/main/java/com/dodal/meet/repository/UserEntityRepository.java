package com.dodal.meet.repository;

import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySocialIdAndSocialType(String socialId, SocialType socialType);
    Optional<UserEntity> findByNickname(String nickname);
}
