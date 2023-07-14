package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "user_tag")
@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity userEntity;

    private String tagName;

    private String tagValue;

    public static UserTagEntity tagEntityToUserTagEntity(UserEntity userEntity, TagEntity tagEntity) {
        return UserTagEntity.builder()
                .userEntity(userEntity)
                .tagName(tagEntity.getName())
                .tagValue(tagEntity.getTagValue())
                .build();
    }

}
