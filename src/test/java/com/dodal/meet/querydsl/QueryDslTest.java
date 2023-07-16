package com.dodal.meet.querydsl;

import com.dodal.meet.fixture.UserEntityFixture;
import com.dodal.meet.fixture.UserTagEntityFixture;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.QUserEntity;
import com.dodal.meet.model.entity.QUserTagEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.model.entity.UserTagEntity;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class QueryDslTest {

    @Autowired
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);
        UserEntity userEntity1 = UserEntityFixture.getUserEntity("1", SocialType.KAKAO);
        UserEntity userEntity2 = UserEntityFixture.getUserEntity("2", SocialType.KAKAO);
        UserEntity userEntity3 = UserEntityFixture.getUserEntity("3", SocialType.KAKAO);
        UserTagEntity userTagEntity1 = UserTagEntityFixture.getUserTagEntity("운동", "001001", userEntity1);
        UserTagEntity userTagEntity2 = UserTagEntityFixture.getUserTagEntity("루틴", "002001", userEntity1);
        UserTagEntity userTagEntity3 = UserTagEntityFixture.getUserTagEntity("기타", "003001", userEntity1);
        UserTagEntity userTagEntity4 = UserTagEntityFixture.getUserTagEntity("운동", "001001", userEntity2);

        em.persist(userEntity1);
        em.persist(userEntity2);
        em.persist(userEntity3);
        em.persist(userTagEntity1);
        em.persist(userTagEntity2);
        em.persist(userTagEntity3);
        em.persist(userTagEntity4);
    }

    @Test
    void selectTest() {
        QUserEntity u = QUserEntity.userEntity;
        UserEntity findUserEntity = queryFactory
                .select(u)
                .from(u)
                .where(u.socialId.eq("1"))
                .fetchOne();

        assertThat(findUserEntity.getSocialId()).isEqualTo("1");
        assertThat(findUserEntity.getSocialType()).isEqualTo(SocialType.KAKAO);
    }

    @Test
    void whereTest() {
        QUserEntity u = QUserEntity.userEntity;
        UserEntity findUserEntity = queryFactory
                .selectFrom(u)
                .where(u.socialId.eq("1")
                        .and(u.socialType.eq(SocialType.KAKAO)))
                .fetchOne();
        assertThat(findUserEntity.getSocialId()).isEqualTo("1");
        assertThat(findUserEntity.getSocialType()).isEqualTo(SocialType.KAKAO);
    }

    @Test
    void orderTest() {
        QUserEntity u = QUserEntity.userEntity;
        List<UserEntity> userEntities = queryFactory
                .selectFrom(u)
                .where(u.socialType.eq(SocialType.KAKAO))
                .orderBy(u.socialId.desc())
                .fetch();
        UserEntity userEntity3 = userEntities.get(0);
        UserEntity userEntity2 = userEntities.get(1);
        UserEntity userEntity1 = userEntities.get(2);

        assertThat(userEntity3.getSocialId()).isEqualTo("3");
        assertThat(userEntity2.getSocialId()).isEqualTo("2");
        assertThat(userEntity1.getSocialId()).isEqualTo("1");
    }

    @Test
    void pagingTest() {
        QUserEntity u = QUserEntity.userEntity;
        List<UserEntity> userEntities = queryFactory
                .selectFrom(u)
                .where(u.socialType.eq(SocialType.KAKAO))
                .orderBy(u.socialId.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(userEntities.size()).isEqualTo(2);
    }

    @Test
    void countTest() {
        QUserEntity u = QUserEntity.userEntity;
        Long total = queryFactory
                .select(u.count())
                .from(u)
                .where(u.socialType.eq(SocialType.KAKAO))
                .orderBy(u.socialId.desc())
                .fetchCount();

        assertThat(total).isEqualTo(3L);
    }

    @Test
    void joinTest() {
        QUserTagEntity ut = QUserTagEntity.userTagEntity;
        QUserEntity u = QUserEntity.userEntity;

        List<UserTagEntity> userTagEntities = queryFactory
                .selectFrom(ut)
                .join(ut.userEntity, u)
                .where(u.socialId.eq("1"))
                .fetch();
        assertThat(userTagEntities.size()).isEqualTo(3);
    }

    @Test
    void joinOnTest() {
        QUserTagEntity ut = QUserTagEntity.userTagEntity;
        QUserEntity u = QUserEntity.userEntity;

        List<UserTagEntity> userTagEntities = queryFactory
                .selectFrom(ut)
                .leftJoin(ut.userEntity, u)
                .on(u.socialId.eq("1"))
                .fetch();
        assertThat(userTagEntities.size()).isEqualTo(4);
    }

    @Test
    void subQueryTest() {
        QUserTagEntity ut = QUserTagEntity.userTagEntity;
        QUserEntity u = QUserEntity.userEntity;

        List<UserTagEntity> userTagEntities = queryFactory
                .selectFrom(ut)
                .where(ut.userEntity.socialId.eq(JPAExpressions.select(u.socialId.max()).from(u)))
                .fetch();

        assertThat(userTagEntities.size()).isEqualTo(0);
    }

    @Test
    void caseTest() {
        QUserEntity u = QUserEntity.userEntity;
        List<String> result = queryFactory
                .select(u.socialId
                        .when("1").then("user1")
                        .when("2").then("user2")
                        .otherwise("user3"))
                .from(u)
                .orderBy(u.socialId.asc())
                .fetch();
        assertThat(result.get(0)).isEqualTo("user1");
        assertThat(result.get(1)).isEqualTo("user2");
        assertThat(result.get(2)).isEqualTo("user3");
    }
}