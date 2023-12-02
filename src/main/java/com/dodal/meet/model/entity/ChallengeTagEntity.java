package com.dodal.meet.model.entity;


import com.dodal.meet.model.BaseTime;
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
public class ChallengeTagEntity extends BaseTime {

    @Id
    @Column(name = "challenge_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "challengeTagEntity")
    private ChallengeRoomEntity challengeRoomEntity;

    @Column(nullable = false, length = 50)
    private String categoryName;

    @Column(nullable = false, length = 50)
    private String categoryValue;

    @Column(nullable = false, length = 50)
    private String tagName;

    @Column(nullable = false, length = 50)
    private String tagValue;

    public void addChallengeRoomEntity(ChallengeRoomEntity challengeRoomEntity) {
        this.challengeRoomEntity = challengeRoomEntity;
    }
}
