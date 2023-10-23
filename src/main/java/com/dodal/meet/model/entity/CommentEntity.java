package com.dodal.meet.model.entity;

import com.dodal.meet.controller.request.feed.CommentCreateRequest;
import com.dodal.meet.utils.DateUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class CommentEntity {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_feed_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeFeedEntity challengeFeedEntity;

    private Long userId;

    private String nickname;

    private String profileUrl;

    private String content;

    private String registeredDate;

    // 셀프 참조

    // 부모 정의
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CommentEntity parent;

    // 자식 정의
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> children = new ArrayList<>();
    private Timestamp registeredAt;

    @PrePersist
    void prePersist() {
        this.registeredAt = Timestamp.from(Instant.now());
        this.registeredDate = DateUtils.getToday();
    }

    public void addChildComment(CommentEntity child) {
        this.children.add(child);
        child.addParent(this);
    }

    public void addParent(CommentEntity parent) {
        this.parent = parent;
    }

    public static CommentEntity toEntity(CommentCreateRequest commentCreateRequest, ChallengeFeedEntity challengeFeedEntity, UserEntity userEntity) {
        return CommentEntity.builder()
                .challengeFeedEntity(challengeFeedEntity)
                .userId(userEntity.getId())
                .profileUrl(userEntity.getProfileUrl())
                .nickname(userEntity.getNickname())
                .content(commentCreateRequest.getContent())
                .build();
    }
}
