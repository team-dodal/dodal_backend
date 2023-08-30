package com.dodal.meet.controller.response.challengeroom;

import com.dodal.meet.controller.request.challengeroom.ChallengeRoomCondition;
import com.dodal.meet.controller.request.challengeroom.ChallengeRoomSearchCategoryRequest;
import com.dodal.meet.controller.response.feed.FeedResponse;
import com.dodal.meet.controller.response.feed.QFeedResponse;
import com.dodal.meet.controller.response.user.QUserCertPerWeek;
import com.dodal.meet.controller.response.user.UserCertPerWeek;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.RoomRole;
import com.dodal.meet.model.RoomSearchType;
import com.dodal.meet.model.entity.*;
import com.dodal.meet.utils.DateUtils;
import com.dodal.meet.utils.FeedUtils;
import com.dodal.meet.utils.OrderByNull;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.*;

@RequiredArgsConstructor
public class ChallengeRoomCustomImpl implements ChallengeRoomCustom{

    private final JPAQueryFactory queryFactory;

    // 도전방 홈 화면 조회 - 검색 조건 (0 : 관심있는 도전, 1 : 인기있는 도전, 2 : 최근 도전)
    @Override
    public Page<ChallengeRoomSearchResponse> getChallengeRooms(final ChallengeRoomCondition challengeRoomCondition, final Pageable pageable, final UserEntity userEntity) {
        final String searchType = challengeRoomCondition.getRoomSearchType().getCode();
        if (!RoomSearchType.isValidSearchType(searchType)) {
            throw new DodalApplicationException(ErrorCode.INVALID_ROOM_SEARCH_TYPE);
        }
        if (searchType.equals(RoomSearchType.INTEREST.getCode())) {
            final String categoryValue = challengeRoomCondition.getCategoryValue();
            if (!StringUtils.hasText(categoryValue)) {
                throw new DodalApplicationException(ErrorCode.NOT_FOUND_CATEGORY);
            }
            return getInterestRooms(userEntity, categoryValue, pageable);
        }
        return getChallengeRooms(userEntity, searchType, pageable);
    }

    @Override
    public Page<ChallengeRoomSearchResponse> getChallengeRoomsByCategory(ChallengeRoomSearchCategoryRequest request, Pageable pageable, UserEntity userEntity) {

        List<Integer> userRoomList = queryFactory.select(challengeUser.challengeRoomEntity.id).from(challengeUser).where(challengeUser.userId.eq(userEntity.getId())).fetch();

        List<ChallengeRoomSearchResponse> content = queryFactory
                .select(new QChallengeRoomSearchResponse(
                        room.id, room.hostId, room.hostNickname, room.hostProfileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue
                )).from(room)
                .innerJoin(challengeUser)
                .on(challengeUser.challengeRoomEntity.eq(room))
                .innerJoin(roomTag)
                .on(roomTag.challengeRoomEntity.eq(room))
                .leftJoin(bookmark)
                .on(bookmark.userEntity.eq(userEntity).and(bookmark.challengeRoomEntity.eq(room)))
                .where(challengeUser.roomRole.eq(RoomRole.HOST).and(tagValueEq(request.getTagValue()))
                        .and(categoryValueEq(request.getCategoryValue())).and(certCntListIn(request.getCertCntList())))
                .orderBy(orderByConditionCode(request.getConditionCode()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Set<Integer> userRoomSet = new HashSet<>(userRoomList);
        content.forEach(row -> {
            if (userRoomSet.contains(row.getChallengeRoomId())) {
                row.setJoinYN("Y");
            }
        });

        return new PageImpl<>(content, pageable, content.size());
    }

    @Override
    public ChallengeRoomDetailResponse getChallengeRoomDetail(final Integer roomId, final UserEntity userEntity) {
        // challengeUser (방장 / 일반 사용자)
        // 조회하려는 유저가 가입한 사용자일 수도 있고, 가입하지 않은 사용자일 수 있다.

        QChallengeUserEntity commonUser = QChallengeUserEntity.challengeUserEntity;
        Long commonUserCnt = queryFactory.
                select(commonUser.count()).
                from(commonUser).
                where(commonUser.challengeRoomEntity.id.eq(roomId).and(commonUser.userId.eq(userEntity.getId()))).fetchOne();


        ChallengeRoomDetailResponse response = queryFactory
                .select(new QChallengeRoomDetailResponse(
                        room.id, room.thumbnailImgUrl, roomTag.tagValue, roomTag.tagName, room.certCnt, room.title,
                        room.hostId, room.hostNickname, room.hostProfileUrl, room.userCnt, room.recruitCnt, room.content,
                        room.certContent, room.certCorrectImgUrl, room.certWrongImgUrl, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.accuseCnt, noti.title, noti.content, room.registeredAt
                )).from(room)
                .innerJoin(room.challengeTagEntity, roomTag)
                .innerJoin(room.challengeUserEntities, challengeUser)
                .leftJoin(bookmark)
                .on(room.eq(bookmark.challengeRoomEntity).and(bookmark.userEntity.id.eq(userEntity.getId())))
                .leftJoin(room.challengeNotiEntities, noti).limit(1)
                .where(room.id.eq(roomId).and(challengeUser.roomRole.eq(RoomRole.HOST)))
                .fetchOne();

        response.setJoinYN(commonUserCnt != 0 ? "Y" : "N");


        String date = DateUtils.parsingTimestamp(Timestamp.from(Instant.now()));

        // 피드 리스트 추가
        // TODO : 오늘 ~ N 일전 데이터 Feed 인증
        List<String> feedUrlList = queryFactory
                .select(feed.certImgUrl)
                .from(feed)
                .innerJoin(room)
                .on(room.id.eq(feed.roomId))
                .where(room.id.eq(roomId).and(feed.certImgUrl.isNotNull()).and(feed.certCode.eq(FeedUtils.CONFIRM)))
                .orderBy(feed.registeredAt.desc())
                .limit(6)
                .fetch();
        response.setFeedUrlList(feedUrlList);

        // 가입한 유저라면 주간 인증 여부 및 인증 이미지 url을 요일별로 반환
        if (commonUserCnt != 0) {
            Map<Integer, String> weekInfo = DateUtils.getWeekInfo();
            List<UserCertPerWeek> userCertPerWeekList = queryFactory
                    .select(new QUserCertPerWeek(feed.registeredDate, feed.certImgUrl))
                    .from(feed)
                    .innerJoin(room)
                    .on(room.id.eq(feed.roomId))
                    .where(feed.userId.eq(userEntity.getId())
                            .and(feed.certCode.eq(FeedUtils.CONFIRM))
                            .and(feed.registeredDate.goe(weekInfo.get(DateUtils.MON)))
                            .and(feed.registeredDate.loe(weekInfo.get(DateUtils.SUN))))
                    .orderBy(feed.registeredDate.asc())
                    .fetch();
            response.setUserCertPerWeekList(userCertPerWeekList);
        }

        // 오늘 인증 여부
        String certCode = queryFactory
                .select(feed.certCode)
                .from(feed)
                .innerJoin(room)
                .on(room.id.eq(feed.roomId))
                .where(room.id.eq(roomId).and(feed.certImgUrl.isNotNull()).and(room.id.eq(roomId))
                        .and(feed.userId.eq(userEntity.getId())).and(feed.registeredDate.eq(date)))
                .orderBy(feed.registeredAt.desc())
                .limit(1)
                .fetchOne();

        response.setTodayCertCode(!StringUtils.hasText(certCode)? FeedUtils.EMPTY : certCode);

        return response;
    }

    @Override
    public List<ChallengeNotiResponse> getChallengeRoomNoti(Integer roomId) {
        List<ChallengeNotiResponse> results = queryFactory
                .select(new QChallengeNotiResponse(noti.id, noti.challengeRoomEntity.id, noti.title, noti.content, Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, {1})", noti.registeredAt, ConstantImpl.create("%Y.%m.%d %r"))))
                .from(noti)
                .where(noti.challengeRoomEntity.id.eq(roomId))
                .orderBy(noti.registeredAt.desc())
                .fetch()
                .stream()
                .peek(x -> x.setDate(DateUtils.parsingString(x.getDate())))
                .collect(Collectors.toList());
        return results;
    }

    private Page<ChallengeRoomSearchResponse> getInterestRooms(UserEntity userEntity, String categoryValue, Pageable pageable) {
        /*
            관심있는 도전 - 사용자가 설정한 태그 정보를 기준으로 해당 태그를 지정한 도전방 정보를 반환한다.
            정렬 기준 - 북마크 많은 순, 최신 순
            연관 테이블 : challenge_room, challenge_tag, challenge_user, users, user_tag
            가입을 하지 않아도 조회할 수 있어야한다.
         */

        List<String> userTagValues = queryFactory.select(userTag.tagValue).from(userTag)
                .innerJoin(user).on(userTag.userEntity.eq(user))
                .where(user.eq(userEntity).and(userTag.tagValue.substring(0,3).eq(categoryValue)))
                .fetch();

        List<ChallengeRoomSearchResponse> content = queryFactory
                .select(new QChallengeRoomSearchResponse(
                        room.id, room.hostId, room.hostNickname, room.hostProfileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue
                )).from(room)
                .innerJoin(room.challengeTagEntity, roomTag)
                .leftJoin(bookmark)
                .on(room.eq(bookmark.challengeRoomEntity).and(bookmark.userEntity.id.eq(userEntity.getId())))
                .where(roomTag.tagValue.in(userTagValues))
                .orderBy(room.bookmarkCnt.desc(), room.registeredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, content.size());
    }

    private Page<ChallengeRoomSearchResponse> getChallengeRooms(UserEntity userEntity, String searchCode, Pageable pageable) {
        /*
            인기 있는 도전 - 북마크 많은 순, 최근 생성 순 기준으로 도전방 정보를 반환한다.
            최근 도전 - 최근 생성 순으로 도전방 정보를 반환한다.
            연관 테이블 : challenge_room, challenge_tag, challenge_user, users, user_tag
            가입을 하지 않아도 조회할 수 있어야한다.
         */
        JPAQuery<ChallengeRoomSearchResponse> query = queryFactory
                .select(new QChallengeRoomSearchResponse(
                        room.id, room.hostId, room.hostNickname, room.hostProfileUrl, room.title, room.certCnt, room.thumbnailImgUrl, room.recruitCnt,
                        room.userCnt, room.bookmarkCnt, new CaseBuilder().when(bookmark.userEntity.isNotNull()).then("Y").otherwise("N").as("bookmarkYN"),
                        room.registeredAt, roomTag.categoryName, roomTag.categoryValue, roomTag.tagName, roomTag.tagValue
                )).from(room)
                .innerJoin(room.challengeTagEntity, roomTag)
                .leftJoin(bookmark)
                .on(room.eq(bookmark.challengeRoomEntity).and(bookmark.userEntity.id.eq(userEntity.getId())))
                ;

        // 인기 있는 도전의 경우 북마크 많은 순으로 추가 정렬
        if (!isEmpty(searchCode)) {
            query.orderBy(orderByBookmarkCnt(searchCode));
        }

        List<ChallengeRoomSearchResponse> content = query
                .orderBy(room.registeredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, content.size());

    }

    @Override
    public Page<FeedResponse> getFeeds(final UserEntity userEntity, final Pageable pageable) {
        List<FeedResponse> content = queryFactory
                .select(new QFeedResponse(
                        room.id, feed.id, room.certCnt, roomTag.categoryName, user.id, user.nickname, challengeUser.continueCertCnt,
                        feed.certImgUrl, feed.certContent, feed.likeCnt, feed.accuseCnt,
                        new CaseBuilder().when(feedLike.isNotNull()).then("Y").otherwise("N").as("likeYN"),
                        feed.registeredDate, feed.registeredAt
                )).from(room)
                .innerJoin(room.challengeTagEntity, roomTag)
                .innerJoin(feed)
                .on(feed.roomId.eq(room.id))
                .innerJoin(user)
                .on(feed.userId.eq(user.id))
                .innerJoin(challengeUser)
                .on(room.id.eq(challengeUser.challengeRoomEntity.id).and(challengeUser.userId.eq(user.id)))
                .leftJoin(feed.feedLikeEntityList, feedLike)
                .on(feedLike.likeUserId.eq(userEntity.getId()))
                .where(feed.certCode.eq(FeedUtils.CONFIRM))
                .orderBy(feed.registeredAt.desc())
                .limit(100)
                .fetch();

        return new PageImpl<>(content, pageable, content.size());

    }

    @Override
    public FeedResponse getFeedOne(final UserEntity userEntity, final Long feedId) {
        return queryFactory
                .select(new QFeedResponse(
                        room.id, feed.id, room.certCnt, roomTag.categoryName, user.id, user.nickname, challengeUser.continueCertCnt,
                        feed.certImgUrl, feed.certContent, feed.likeCnt, feed.accuseCnt,
                        new CaseBuilder().when(feedLike.isNotNull()).then("Y").otherwise("N").as("likeYN"),
                        feed.registeredDate, feed.registeredAt
                )).from(room)
                .innerJoin(room.challengeTagEntity, roomTag)
                .innerJoin(feed)
                .on(feed.roomId.eq(room.id))
                .innerJoin(user)
                .on(feed.userId.eq(user.id))
                .innerJoin(challengeUser)
                .on(room.id.eq(challengeUser.challengeRoomEntity.id).and(challengeUser.userId.eq(user.id)))
                .leftJoin(feed.feedLikeEntityList, feedLike)
                .on(feedLike.likeUserId.eq(userEntity.getId()))
                .where(feed.certCode.eq(FeedUtils.CONFIRM).and(feed.id.eq(feedId)))
                .fetchOne();
    }

    private OrderSpecifier<?> orderByBookmarkCnt(final String searchCode) {
        return searchCode.equals(RoomSearchType.POPULARITY.getCode()) ? room.bookmarkCnt.desc() : OrderByNull.getDefault();
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
    QChallengeNotiEntity noti = QChallengeNotiEntity.challengeNotiEntity;
    QFeedLikeEntity feedLike = QFeedLikeEntity.feedLikeEntity;
}
