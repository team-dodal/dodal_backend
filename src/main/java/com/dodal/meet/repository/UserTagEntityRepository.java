package com.dodal.meet.repository;

import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.model.entity.UserTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserTagEntityRepository extends JpaRepository<UserTagEntity, Long> {
    List<UserTagEntity> findAllByUserEntity(UserEntity entity);
}
