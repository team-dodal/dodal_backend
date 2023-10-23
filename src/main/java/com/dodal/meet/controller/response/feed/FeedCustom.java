package com.dodal.meet.controller.response.feed;

import java.util.List;

public interface FeedCustom {

    List<CommentResponse> getFeedComments(Long feedId);
}
