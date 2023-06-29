package com.dodal.meet.model.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "token")
@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Accessors(chain = true)
public class TokenEntity {

    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "tokenEntity", fetch = FetchType.LAZY)
    private UserEntity userEntity;
    private String refreshToken;
    private String fcmToken;

    public void updateRefreshToken(String refreshToken) {
        Assert.notNull(refreshToken, "refreshToken must not be null");
        this.refreshToken = refreshToken;
    }

    public void updateFcmToken(String fcmToken) {
        Assert.notNull(fcmToken, "fcmToken must not be null");
        this.fcmToken = fcmToken;
    }
}
