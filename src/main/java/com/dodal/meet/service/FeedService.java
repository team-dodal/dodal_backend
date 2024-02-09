package com.dodal.meet.service;


import com.dodal.meet.controller.request.fcm.FcmKafkaPush;
import com.dodal.meet.controller.request.feed.CommentCreateRequest;
import com.dodal.meet.controller.request.feed.CommentUpdateRequest;
import com.dodal.meet.controller.response.feed.CommentResponse;
import com.dodal.meet.controller.response.feed.FeedResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.ChallengeFeedEntity;
import com.dodal.meet.model.entity.CommentEntity;
import com.dodal.meet.model.entity.FeedLikeEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.producer.PushProducer;
import com.dodal.meet.repository.*;
import com.dodal.meet.utils.DtoUtils;
import com.dodal.meet.utils.MessageType;
import com.dodal.meet.utils.MessageUtils;
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

    private final PushProducer pushProducer;
    private final AlarmService alarmService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<FeedResponse> getRoomFeeds(final User user, final Integer roomId, final Pageable pageable) {
        challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));

        final UserEntity userEntity = userService.getCachedUserEntity(user);
        return challengeRoomEntityRepository.getRoomFeeds(userEntity, roomId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<FeedResponse> getFeeds(final User user, final Pageable pageable) {
        final UserEntity userEntity = userService.getCachedUserEntity(user);
        return challengeRoomEntityRepository.getFeeds(userEntity, pageable);
    }

    @Transactional
    public void postFeedLike(final Long feedId, final User user) {
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userService.getCachedUserEntity(user);
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

        alarmService.saveAlarmHist(MessageUtils.makeAlarmHistResponse(MessageType.FEED_LIKE, feedEntity.getRoomTitle(), feedEntity.getUserId(), feedEntity.getRoomId()));
        pushProducer.send(FcmKafkaPush.makeKafkaPush(feedEntity.getUserId(),
                MessageUtils.makeFcmPushRequest(MessageType.FEED_LIKE, feedEntity.getRoomTitle())));
    }

    @Transactional
    public void deleteFeedLike(final Long feedId, final User user) {
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userService.getCachedUserEntity(user);
        FeedLikeEntity findFeedLikeEntity = feedLikeEntityRepository.findByFeedInfo(feedId, userEntity.getId()).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED_LIKE));
        feedLikeEntityRepository.deleteById(findFeedLikeEntity.getId());
        feedEntity.updateLikeCntByNum(DtoUtils.MINUS_ONE);
    }

    @Transactional
    public List<CommentResponse> postFeedComment(final Long feedId, final User user, final CommentCreateRequest commentCreateRequest) {
        ChallengeFeedEntity feedEntity = challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userService.getCachedUserEntity(user);

        CommentEntity newComment = CommentEntity.newInstance(commentCreateRequest, feedEntity, userEntity);

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

        // 피드 작성자와 댓글 요청자가 다른 경우 작성자에게 PUSH 알림
        if (userEntity.getId() != feedEntity.getUserId()) {
            alarmService.saveAlarmHist(MessageUtils.makeAlarmHistResponse(MessageType.FEED_COMMENT, feedEntity.getRoomTitle(), feedEntity.getUserId(), feedEntity.getRoomId()));
            pushProducer.send(FcmKafkaPush.makeKafkaPush(feedEntity.getUserId(),
                    MessageUtils.makeFcmPushRequest(MessageType.FEED_COMMENT, feedEntity.getRoomTitle())));
        }
        return getFeedComments(feedId);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getFeedComments(Long feedId) {
        return challengeFeedEntityRepository.getFeedComments(feedId);
    }

    @Transactional
    public List<CommentResponse> updateFeedComment(final Long feedId, final User user, final CommentUpdateRequest commentUpdateRequest) {
        challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userService.getCachedUserEntity(user);
        CommentEntity commentEntity = commentEntityRepository.findById(commentUpdateRequest.getCommentId()).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_COMMENT));
        if (commentEntity.getUserId() != userEntity.getId()) {
            throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_COMMENT);
        }
        commentEntity.updateContent(commentUpdateRequest.getContent());

        return getFeedComments(feedId);
    }

    @Transactional(readOnly = true)
    public FeedResponse getFeed(final User user, final Long feedId) {
        challengeFeedEntityRepository.findById(feedId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_FEED));
        final UserEntity userEntity = userService.getCachedUserEntity(user);
        return challengeFeedEntityRepository.getFeed(userEntity, feedId);
    }
}
