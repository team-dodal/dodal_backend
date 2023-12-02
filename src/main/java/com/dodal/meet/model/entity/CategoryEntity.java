package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Builder.Default
    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TagEntity> tagEntities = new ArrayList<>();

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String categoryValue;

    @Column(nullable = true)
    private String emoji;

    @Column(nullable = true)
    private String subName;

    @Builder.Default
    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HashTagEntity> hashTagEntities = new ArrayList<>();
}
