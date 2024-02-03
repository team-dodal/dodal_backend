package com.dodal.meet.repository;

import com.dodal.meet.model.entity.CategoryEntity;
import com.dodal.meet.model.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryEntityRepository extends JpaRepository<CategoryEntity, Integer> {

    List<CategoryEntity> findAllByOrderByIdAsc();

    Optional<CategoryEntity> findByCategoryValue(String categoryValue);

    @Query(
            "SELECT distinct c FROM CategoryEntity c JOIN FETCH c.hashTagEntities WHERE c.categoryValue IN " +
            "(SELECT distinct substring(t.tagValue, 1, 3) FROM TagEntity t WHERE t IN :tagEntityList)"
    )
    List<CategoryEntity> findAllByTagEntity(@Param("tagEntityList") List<TagEntity> tagEntityList);
}
