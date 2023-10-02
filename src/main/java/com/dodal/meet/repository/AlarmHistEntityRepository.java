package com.dodal.meet.repository;

import com.dodal.meet.model.entity.AlarmHistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmHistEntityRepository extends JpaRepository<AlarmHistEntity, Long> {

    @Query(
            "SELECT  a " +
                    "FROM AlarmHistEntity a " +
                    "WHERE a.userId = :userId " +
                    "ORDER BY a.registeredAt DESC "
    )
    List<AlarmHistEntity> findAllByUserId(@Param("userId") Long userId);

    void deleteAllByUserId(Long userId);

}
