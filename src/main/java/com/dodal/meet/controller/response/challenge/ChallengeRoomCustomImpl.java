package com.dodal.meet.controller.response.challenge;

import com.dodal.meet.controller.request.challengeRoom.ChallengeRoomCondition;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.RoomSearchType;
import com.dodal.meet.model.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

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

    QUserEntity user = QUserEntity.userEntity;
    QChallengeRoomEntity room = QChallengeRoomEntity.challengeRoomEntity;
    QChallengeUserEntity challengeUser = QChallengeUserEntity.challengeUserEntity;
    QChallengeTagEntity roomTag = QChallengeTagEntity.challengeTagEntity;
    QChallengeBookmarkEntity bookmark = QChallengeBookmarkEntity.challengeBookmarkEntity;
    QUserTagEntity userTag = QUserTagEntity.userTagEntity;
}