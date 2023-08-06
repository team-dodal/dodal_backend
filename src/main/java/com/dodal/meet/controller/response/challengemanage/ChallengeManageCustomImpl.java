package com.dodal.meet.controller.response.challengemanage;

import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.utils.DateUtils;
import com.dodal.meet.utils.FeedUtils;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ChallengeManageCustomImpl implements ChallengeManageCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChallengeUserRoleResponse> getChallengeUser(UserEntity userEntity) {
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
                .fetch();

        return content;
    }

    @Override
    public List<ChallengeHostRoleResponse> getChallengeHost(UserEntity userEntity) {

        StringPath certRequestCnt = Expressions.stringPath("certRequestCnt");

        List<ChallengeHostRoleResponse> content = queryFactory
                .select(new QChallengeHostRoleResponse(
                        room.id, room.hostId, room.hostNickname, room.hostProfileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue,
                        ExpressionUtils.as(JPAExpressions.
                                select(feed.count().intValue()).
                                from(feed).
                                where(room.id.eq(feed.roomId).and(feed.certCode.eq(FeedUtils.REQUEST))), "certRequestCnt")
                ))
                .from(room)
                .innerJoin(room.challengeUserEntities, challengeUser)
                .innerJoin(room.challengeTagEntity, roomTag)
                .leftJoin(bookmark)
                .on(bookmark.challengeRoomEntity.eq(room).and(bookmark.userEntity.eq(userEntity)))
                .where(challengeUser.userId.eq(userEntity.getId()).and(challengeUser.roomRole.eq(RoomRole.HOST)))
                .orderBy(certRequestCnt.desc())
                .fetch();
        return content;
    }

    @Override
    public List<ChallengeCertImgManage> getCertImgList(final Integer roomId, final String dateYM) {
        List<ChallengeCertImgManage> content = queryFactory
                .select(new QChallengeCertImgManage(room.id, feed.id, feed.userId, feed.certImgUrl, feed.certContent, feed.certCode, feed.registeredAt, feed.registeredDate))
                .from(room)
                .innerJoin(feed)
                .on(room.id.eq(feed.roomId))
                .where(room.id.eq(roomId).and(feed.registeredDate.substring(0, 6).eq(dateYM)))
                .orderBy(feed.registeredDate.desc(), feed.registeredAt.desc())
                .fetch();
        return content;
    }

    QUserEntity user = QUserEntity.userEntity;
    QChallengeRoomEntity room = QChallengeRoomEntity.challengeRoomEntity;
    QChallengeUserEntity challengeUser = QChallengeUserEntity.challengeUserEntity;
    QChallengeTagEntity roomTag = QChallengeTagEntity.challengeTagEntity;
    QChallengeBookmarkEntity bookmark = QChallengeBookmarkEntity.challengeBookmarkEntity;
    QChallengeFeedEntity feed = QChallengeFeedEntity.challengeFeedEntity;
    QChallengeNotiEntity noti = QChallengeNotiEntity.challengeNotiEntity;


}
