package com.dodal.meet.repository;

import com.dodal.meet.model.entity.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommonCodeEntityRepository extends JpaRepository<CommonCodeEntity, Long> {

    @Query(
            "SELECT  c " +
            "FROM CommonCodeEntity c " +
            "WHERE c.category = :category " +
            "AND c.status = 'ACTIVE' " +
            "ORDER BY c.id ASC"
    )
    List<CommonCodeEntity> findAllByCategory(@Param("category") String category);

}
