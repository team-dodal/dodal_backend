package com.dodal.meet.controller.response.feed;


import com.dodal.meet.model.entity.CommentEntity;
import com.dodal.meet.model.entity.QCommentEntity;
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

    QCommentEntity comment = QCommentEntity.commentEntity;

}
