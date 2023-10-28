package com.dodal.meet.controller.response.feed;


import com.dodal.meet.model.entity.*;
import com.dodal.meet.utils.DtoUtils;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FeedCustomImpl implements FeedCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<CommentResponse> getFeedComments(Long feedId) {

        List<CommentEntity> commentEntityList = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.challengeFeedEntity.id.eq(feedId))
                .orderBy(comment.parent.id.asc().nullsFirst(), comment.registeredDate.asc())
                .fetch();

        List<CommentResponse> result = new ArrayList<>();
        Map<Long, CommentResponse> map = new HashMap<>();
        commentEntityList.forEach(e -> {
            CommentResponse dto = CommentResponse.convertCommentToDto(e);
            map.put(dto.getCommentId(), dto);
            if (ObjectUtils.isEmpty(e.getParent())) {
                result.add(dto);
            } else {
                // 부모가 있는 경우 대댓글
                map.get(dto.getParentId()).getChildren().add(dto);
            }
        });
        return result;
    }

    @Override
    public FeedResponse getFeed(UserEntity userEntity, Long feedId) {
        FeedResponse feedResponse = queryFactory
                .select(new QFeedResponse(
                        room.id, room.title, feed.id, room.certCnt, roomTag.categoryName, user.id, user.nickname, challengeUser.continueCertCnt,
                        feed.certImgUrl, feed.certContent, feed.likeCnt, feed.commentCnt, feed.accuseCnt,
                        new CaseBuilder().when(feedLike.isNotNull()).then("Y").otherwise("N").as("likeYN"),
                        feed.registeredDate, feed.registeredAt
                )).from(room)
                .innerJoin(room.challengeTagEntity, roomTag)
                .innerJoin(feed)
                .on(feed.roomId.eq(room.id))
                .innerJoin(user)
                .on(feed.userId.eq(user.id))
                .innerJoin(challengeUser)
                .on(room.id.eq(challengeUser.challengeRoomEntity.id).and(challengeUser.userEntity.id.eq(user.id)))
                .leftJoin(feed.feedLikeEntityList, feedLike)
                .on(feedLike.likeUserId.eq(userEntity.getId()))
                .where(feed.id.eq(feedId))
                .fetchOne();
        // 마이페이지 조회 건으로 Y로 사용
        feedResponse.setJoinYN(DtoUtils.Y);
        return feedResponse;
    }


    QUserEntity user = QUserEntity.userEntity;
    QChallengeRoomEntity room = QChallengeRoomEntity.challengeRoomEntity;
    QChallengeUserEntity challengeUser = QChallengeUserEntity.challengeUserEntity;
    QChallengeTagEntity roomTag = QChallengeTagEntity.challengeTagEntity;
    QChallengeFeedEntity feed = QChallengeFeedEntity.challengeFeedEntity;
    QFeedLikeEntity feedLike = QFeedLikeEntity.feedLikeEntity;
    QCommentEntity comment = QCommentEntity.commentEntity;

}
