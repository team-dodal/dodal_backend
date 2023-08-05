package com.dodal.meet.controller.response.challengelist;

import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.utils.DateUtils;
import com.dodal.meet.utils.FeedUtils;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ChallengeListCustomImpl implements ChallengeListCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChallengeUserRoleResponse> getChallengeUser(Pageable pageable, UserEntity userEntity) {
        Map<Integer, String> weekInfo = DateUtils.getWeekInfo();
        String today = DateUtils.parsingTimestamp(Timestamp.from(Instant.now()));

        List<ChallengeUserRoleResponse> content = queryFactory
                .select(new QChallengeUserRoleResponse(
                        room.id, room.hostId, room.hostNickname, room.hostProfileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue,
                        ExpressionUtils.as(JPAExpressions.
                                        select(feed.count().intValue()).
                                        from(feed).
                                        innerJoin(room).
                                        on(room.id.eq(feed.roomId)).
                                        where(
                                                feed.userId.eq(userEntity.getId()).
                                                and(feed.certCode.eq(FeedUtils.CONFIRM)).
                                                and(feed.registeredDate.goe(weekInfo.get(DateUtils.MON))).
                                                and(feed.registeredDate.loe(weekInfo.get(DateUtils.SUN))))
                                , "weekUserCertCnt"),
                        new CaseBuilder().when(feed.certCode.isNotNull()).then(feed.certCode).otherwise(FeedUtils.EMPTY).as("certCode")
                ))
                .from(room)
                .innerJoin(room.challengeUserEntities, challengeUser)
                .innerJoin(room.challengeTagEntity, roomTag)
                .leftJoin(bookmark)
                .on(bookmark.challengeRoomEntity.eq(room).and(bookmark.userEntity.eq(userEntity)))
                .leftJoin(feed)
                .on(room.id.eq(feed.roomId).and(feed.registeredDate.eq(today)))
                .where(challengeUser.roomRole.eq(RoomRole.USER).and(challengeUser.userId.eq(userEntity.getId())))
                .orderBy(feed.certCode.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(content, pageable, content.size());
    }


    QUserEntity user = QUserEntity.userEntity;
    QChallengeRoomEntity room = QChallengeRoomEntity.challengeRoomEntity;
    QChallengeUserEntity challengeUser = QChallengeUserEntity.challengeUserEntity;
    QChallengeTagEntity roomTag = QChallengeTagEntity.challengeTagEntity;
    QChallengeBookmarkEntity bookmark = QChallengeBookmarkEntity.challengeBookmarkEntity;
    QChallengeFeedEntity feed = QChallengeFeedEntity.challengeFeedEntity;
    QChallengeNotiEntity noti = QChallengeNotiEntity.challengeNotiEntity;

    @Override
    public Page<ChallengeUserRoleResponse> getChallengeHost(Pageable pageable, UserEntity userEntity) {
        return null;
    }

}
