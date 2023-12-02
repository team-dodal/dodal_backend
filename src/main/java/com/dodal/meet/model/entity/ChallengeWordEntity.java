package com.dodal.meet.model.entity;

import com.dodal.meet.model.BaseTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;

@Entity
@Table(name = "challenge_word")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class ChallengeWordEntity extends BaseTime {

    @Id
    @Column(name = "challenge_word_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private Long userId;

}
