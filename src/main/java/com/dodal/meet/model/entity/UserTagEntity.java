package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private String tagName;

    private String tagValue;

    public static UserTagEntity tagEntityToUserTagEntity(UserEntity userEntity, TagEntity tagEntity) {
        return UserTagEntity.builder()
                .userEntity(userEntity)
                .tagName(tagEntity.getName())
                .tagValue(tagEntity.getValue())
                .build();
    }

}
