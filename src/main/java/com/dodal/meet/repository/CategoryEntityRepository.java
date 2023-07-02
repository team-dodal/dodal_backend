package com.dodal.meet.repository;

import com.dodal.meet.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryEntityRepository extends JpaRepository<CategoryEntity, Integer> {

    List<CategoryEntity> findAllByOrderByIdAsc();
}
