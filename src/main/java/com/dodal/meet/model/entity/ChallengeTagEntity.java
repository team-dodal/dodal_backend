package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "challenge_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeTagEntity {

    @Id
    @Column(name = "challenge_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "challengeTagEntity")
    private ChallengeRoomEntity challengeRoomEntity;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_value")
    private String categoryValue;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "tag_value")
    private String tagValue;

    public void addChallengeRoomEntity(ChallengeRoomEntity challengeRoomEntity) {
        this.challengeRoomEntity = challengeRoomEntity;
    }
}
