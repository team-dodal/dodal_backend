package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class TagEntity {

    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tagValue;
}
