package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "common_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Builder
public class CommonCodeEntity {
    @Id
    @Column(name = "common_code_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String categoryName;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 50)
    private String codeName;

    @Column(nullable = false, length = 50)
    private String status;

    @PrePersist
    void prePersist() {
        this.status = "ACTIVE";
    }

}
