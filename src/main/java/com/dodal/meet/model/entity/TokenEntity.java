package com.dodal.meet.model.entity;


import com.dodal.meet.model.BaseTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.Accessors;
import javax.persistence.*;

@Entity
@Table(name = "token")
@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Accessors(chain = true)
public class TokenEntity extends BaseTime {

    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = true)
    private String fcmToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
