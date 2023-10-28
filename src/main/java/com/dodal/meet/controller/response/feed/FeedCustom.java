package com.dodal.meet.controller.response.feed;

import com.dodal.meet.model.entity.UserEntity;

import java.util.List;

public interface FeedCustom {

    List<CommentResponse> getFeedComments(Long feedId);

    FeedResponse getFeed(UserEntity userEntity, Long feedId);
}
