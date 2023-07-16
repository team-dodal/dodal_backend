package com.dodal.meet.repository;

import com.dodal.meet.fixture.UserEntityFixture;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.QUserEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserEntityRepositoryTest {

    @Autowired
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);
        UserEntity userEntity = UserEntityFixture.getUserEntity("1", SocialType.KAKAO);
        em.persist(userEntity);
    }

    @Test
    void queryDsl() {
//        QUserEntity u = new QUserEntity("u");
        QUserEntity u = QUserEntity.userEntity;
        UserEntity findUserEntity = queryFactory
                .select(u)
                .from(u)
                .where(u.socialId.eq("1"))
                .fetchOne();

        assertThat(findUserEntity.getSocialId()).isEqualTo("1");
        assertThat(findUserEntity.getSocialType()).isEqualTo(SocialType.KAKAO);
    }
}