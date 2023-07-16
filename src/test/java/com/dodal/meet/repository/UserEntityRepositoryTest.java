package com.dodal.meet.repository;

import com.dodal.meet.model.entity.QUserEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class UserEntityRepositoryTest {

    @Autowired
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    @BeforeEach
    void createTest() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    void queryDsl() {
        QUserEntity user = new QUserEntity("u");
        List<UserEntity> users = queryFactory.select(user).from(user).fetch();
        Assertions.assertThat(users.size() == 0).isTrue();
    }
}