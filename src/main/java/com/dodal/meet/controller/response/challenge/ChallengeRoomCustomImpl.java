package com.dodal.meet.controller.response.challenge;

import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCondition;
import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomSearchCategoryRequest;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.RoomSearchType;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.utils.DateUtils;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import static org.springframework.util.ObjectUtils.*;

@RequiredArgsConstructor
public class ChallengeRoomCustomImpl implements ChallengeRoomCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChallengeRoomSearchResponse> getChallengeRooms(final ChallengeRoomCondition challengeRoomCondition, final Pageable pageable, final UserEntity userEntity) {
        final String searchType = challengeRoomCondition.getRoomSearchType().name();
        if (!RoomSearchType.isValidSearchType(searchType)) {
            throw new DodalApplicationException(ErrorCode.INVALID_ROOM_SEARCH_TYPE);
        }
        if (searchType.equals(RoomSearchType.INTEREST.getCode())) {
            final String tagValue = challengeRoomCondition.getTagValue();
            if (!StringUtils.hasText(tagValue)) {
                throw new DodalApplicationException(ErrorCode.NOT_FOUND_TAG);
            }
            return getInterestRooms(pageable, userEntity, tagValue);
        }
        return getChallengeRooms(searchType, pageable, userEntity);
    }

    @Override
    public Page<ChallengeRoomSearchResponse> getChallengeRoomsByCategory(ChallengeRoomSearchCategoryRequest request, Pageable pageable, UserEntity userEntity) {
        QueryResults<ChallengeRoomSearchResponse> results = queryFactory
                .select(new QChallengeRoomSearchResponse(
                        room.id, challengeUser.userId, challengeUser.nickname, user.profileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue
                )).from(room)
                .innerJoin(challengeUser)
                .on(challengeUser.challengeRoomEntity.eq(room))
                .innerJoin(roomTag)
                .on(roomTag.challengeRoomEntity.eq(room))
                .innerJoin(user)
                .on(challengeUser.userId.eq(user.id))
                .leftJoin(bookmark)
                .on(bookmark.userEntity.eq(userEntity).and(bookmark.challengeRoomEntity.eq(room)))
                .where(challengeUser.roomRole.eq(RoomRole.HOST).and(tagValueEq(request.getTagValue()))
                        .and(categoryValueEq(request.getCategoryValue())).and(certCntListIn(request.getCertCntList())))
                .orderBy(orderByConditionCode(request.getConditionCode()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults()
                ;

        List<ChallengeRoomSearchResponse> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public ChallengeRoomDetailResponse getChallengeRoomDetail(final Integer roomId, final UserEntity userEntity) {
        ChallengeRoomDetailResponse response = queryFactory
                .select(new QChallengeRoomDetailResponse(
                        room.id, room.thumbnailImgUrl, roomTag.tagValue, roomTag.tagName, room.certCnt, room.title,
                        challengeUser.userId, challengeUser.nickname, room.userCnt, room.recruitCnt, room.content,
                        room.certContent, room.certCorrectImgUrl, room.certWrongImgUrl, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        new CaseBuilder().when(user.isNotNull()).then("Y").otherwise("N").as("joinYN"), room.accuseCnt, room.noticeContent, room.registeredAt
                )).from(room)
                .innerJoin(challengeUser)
                .on(challengeUser.challengeRoomEntity.eq(room))
                .innerJoin(roomTag)
                .on(roomTag.challengeRoomEntity.eq(room))
                .leftJoin(user)
                .on(challengeUser.userId.eq(user.id))
                .leftJoin(bookmark)
                .on(bookmark.userEntity.eq(userEntity).and(bookmark.challengeRoomEntity.eq(room)))
                .where(challengeUser.roomRole.eq(RoomRole.HOST).and(room.id.eq(roomId)))
                .fetchOne();

        // 피드 리스트 추가
        List<String> feedUrlList = queryFactory
                .select(feed.certImgUrl)
                .from(feed)
                .innerJoin(room)
                .on(room.id.eq(feed.roomId))
                .where(room.id.eq(roomId).and(feed.certImgUrl.isNotNull()))
                .orderBy(feed.registeredAt.desc())
                .fetch();
        response.setFeedUrlList(feedUrlList);

        // 오늘 인증 여부
        String date = DateUtils.parsingTimestamp(Timestamp.from(Instant.now()));
        long certCnt = queryFactory
                .select(feed.count())
                .from(feed)
                .innerJoin(room)
                .on(room.id.eq(feed.roomId))
                .where(room.id.eq(roomId).and(feed.certImgUrl.isNotNull())
                        .and(feed.userId.eq(userEntity.getId())).and(feed.registeredDate.eq(date)))
                .fetchCount();

        response.setTodayCertYN(certCnt != 0 ? "Y" : "N");

        return response;
    }

    private Page<ChallengeRoomSearchResponse> getInterestRooms(Pageable pageable, UserEntity userEntity, String tagValue) {
        QueryResults<ChallengeRoomSearchResponse> results = queryFactory
                .select(new QChallengeRoomSearchResponse(
                        room.id, challengeUser.userId, challengeUser.nickname, user.profileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue
                )).from(room)
                .innerJoin(challengeUser)
                .on(challengeUser.challengeRoomEntity.eq(room))
                .innerJoin(roomTag)
                .on(roomTag.challengeRoomEntity.eq(room))
                .innerJoin(user)
                .on(challengeUser.userId.eq(user.id))
                .leftJoin(bookmark)
                .on(bookmark.userEntity.eq(userEntity).and(bookmark.challengeRoomEntity.eq(room)))
                .where(challengeUser.roomRole.eq(RoomRole.HOST).and(roomTag.tagValue.eq(tagValue)))
                .orderBy(room.bookmarkCnt.desc(), room.registeredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults()
                ;

        List<ChallengeRoomSearchResponse> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private Page<ChallengeRoomSearchResponse> getChallengeRooms(String searchCode, Pageable pageable, UserEntity userEntity) {

        JPAQuery<ChallengeRoomSearchResponse> query = queryFactory
                .select(new QChallengeRoomSearchResponse(
                        room.id, challengeUser.userId, challengeUser.nickname, user.profileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue
                )).from(room)
                .innerJoin(challengeUser)
                .on(challengeUser.challengeRoomEntity.eq(room))
                .innerJoin(roomTag)
                .on(roomTag.challengeRoomEntity.eq(room))
                .innerJoin(user)
                .on(challengeUser.userId.eq(user.id))
                .leftJoin(bookmark)
                .on(bookmark.userEntity.eq(userEntity).and(bookmark.challengeRoomEntity.eq(room)))
                .where(challengeUser.roomRole.eq(RoomRole.HOST))
                ;

        if (!isEmpty(orderByBookmarkCnt(searchCode))) {
            query.orderBy(orderByBookmarkCnt(searchCode));
        }

        QueryResults<ChallengeRoomSearchResponse> results = query
                .orderBy(room.registeredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ChallengeRoomSearchResponse> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);

    }

    private OrderSpecifier<?> orderByBookmarkCnt(final String searchCode) {
        return searchCode.equals(RoomSearchType.INTEREST.getCode()) ? room.bookmarkCnt.desc() : null;
    }

    private OrderSpecifier<?> orderByConditionCode(final String conditionCode) {
        switch (conditionCode) {
            // 인기순
            case "":
            case "0":
                return room.bookmarkCnt.desc();
            // 최신순
            case "1":
                return room.registeredAt.desc();
            // 유저 많은 순
            case "2":
                return room.userCnt.desc();
            // 유저 적은 순
            default:
                return room.userCnt.asc();
        }
    }

    private BooleanExpression categoryValueEq(String categoryValue) {
        return isEmpty(categoryValue) ? null : roomTag.categoryValue.eq(categoryValue);
    }

    private BooleanExpression tagValueEq(String tagValue) {
        return isEmpty(tagValue) ? null : roomTag.tagValue.eq(tagValue);
    }

    private BooleanExpression certCntListIn(List<Integer> certCntList) {
        return isEmpty(certCntList) ? null : room.certCnt.in(certCntList);
    }

    QUserEntity user = QUserEntity.userEntity;
    QChallengeRoomEntity room = QChallengeRoomEntity.challengeRoomEntity;
    QChallengeUserEntity challengeUser = QChallengeUserEntity.challengeUserEntity;
    QChallengeTagEntity roomTag = QChallengeTagEntity.challengeTagEntity;
    QChallengeBookmarkEntity bookmark = QChallengeBookmarkEntity.challengeBookmarkEntity;
    QUserTagEntity userTag = QUserTagEntity.userTagEntity;
    QChallengeFeedEntity feed = QChallengeFeedEntity.challengeFeedEntity;
}
