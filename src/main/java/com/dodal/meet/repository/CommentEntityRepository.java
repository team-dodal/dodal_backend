package com.dodal.meet.repository;

import com.dodal.meet.model.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {

	@Modifying
	@Query(
		"UPDATE CommentEntity c " +
			"SET c.nickname = :nickname, c.profileUrl = :profileUrl " +
			"WHERE c.userId = :userId"
	)
	void updateNicknameAndProfileUrlByUserId(@Param("userId") Long userId, @Param("nickname") String nickname, @Param("profileUrl") String profileUrl);
}
