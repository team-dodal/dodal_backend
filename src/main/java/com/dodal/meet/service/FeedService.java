package com.dodal.meet.service;


import com.dodal.meet.controller.request.feed.CommentCreateRequest;
import com.dodal.meet.controller.response.feed.CommentResponse;
import com.dodal.meet.controller.response.feed.FeedResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.ChallengeFeedEntity;
import com.dodal.meet.model.entity.CommentEntity;
import com.dodal.meet.model.entity.FeedLikeEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.DtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final CommentEntityRepository commentEntityRepository;
    private final UserEntityRepository userEntityRepository;

    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;
    private final FeedLikeEntityRepository feedLikeEntityRepository;
    private final ChallengeFeedEntityRepository challengeFeedEntityRepository;

    @Transactional(readOnly = true)
    public Page<FeedResponse> getRoomFeeds(final User user, final Integer roomId, final Pageable pageable) {
        challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));

        final UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        return challengeRoomEntityRepository.getRoomFeeds(userEntity, roomId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<FeedResponse> getFeeds(final User user, final Pageable pageable) {
        final UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        return challengeRoomEntityRepository.getFeeds(userEntity, pageable);
    }

    @Transactional
    public FeedResponse postFeedLike(final Long feedId, final User user) {
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        FeedLikeEntity findFeedLikeEntity = feedLikeEntityRepository.findByFeedInfo(feedId, userEntity.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(findFeedLikeEntity)) {
            throw new DodalApplicationException(ErrorCode.INVALID_FEED_LIKE_REQUEST);
        }
        FeedLikeEntity feedLikeEntity = FeedLikeEntity.builder()
                .challengeFeedEntity(feedEntity)
                .likeUserId(userEntity.getId())
                .build();

        feedLikeEntityRepository.save(feedLikeEntity);
        feedEntity.updateLikeCntByNum(DtoUtils.ONE);

        return challengeRoomEntityRepository.getFeedOne(userEntity, feedId);
    }

    @Transactional
    public FeedResponse deleteFeedLike(Long feedId, User user) {
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        FeedLikeEntity findFeedLikeEntity = feedLikeEntityRepository.findByFeedInfo(feedId, userEntity.getId()).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED_LIKE));
        feedLikeEntityRepository.deleteById(findFeedLikeEntity.getId());
        feedEntity.updateLikeCntByNum(DtoUtils.MINUS_ONE);

        return challengeRoomEntityRepository.getFeedOne(userEntity, feedId);
    }

    @Transactional
    public List<CommentResponse> postFeedComment(Long feedId, User user, CommentCreateRequest commentCreateRequest) {
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userEntityRepository.findBySocialIdAndSocialType(user.getSocialId(), user.getSocialType()).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));

        CommentEntity newComment = CommentEntity.toEntity(commentCreateRequest, feedEntity, userEntity);

        CommentEntity commentEntity;
        // 대댓글인 경우
        if (commentCreateRequest.getParentId() != null) {
            commentEntity = commentEntityRepository.findById(commentCreateRequest.getParentId()).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_COMMENT));
            commentEntity.addChildComment(newComment);
        }
        // 신규 댓글인 경우
        else {
            commentEntityRepository.save(newComment);
        }

        // FeedEntity Comment 갯수 1개 업데이트
        feedEntity.updateCommentCntByNum(DtoUtils.ONE);
        return getFeedComments(feedId);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getFeedComments(Long feedId) {
        return challengeFeedEntityRepository.getFeedComments(feedId);
    }
}
