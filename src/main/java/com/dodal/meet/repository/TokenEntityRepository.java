package com.dodal.meet.repository;

import com.dodal.meet.model.entity.TokenEntity;
import com.dodal.meet.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TokenEntityRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByUserEntity(UserEntity userEntity);
}
