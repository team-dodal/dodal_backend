package com.dodal.meet.controller.response.feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@ToString
public class FeedMaxRegist {

    private Integer roomId;
    private Timestamp registeredAt;

    @QueryProjection

    public FeedMaxRegist(Integer roomId, Timestamp registeredAt) {
        this.roomId = roomId;
        this.registeredAt = registeredAt;
    }
}
