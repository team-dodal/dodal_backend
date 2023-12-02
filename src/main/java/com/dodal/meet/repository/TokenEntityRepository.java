package com.dodal.meet.repository;

import com.dodal.meet.model.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenEntityRepository extends JpaRepository<TokenEntity, Long> {
}
