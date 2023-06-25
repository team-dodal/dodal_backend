package com.dodal.meet.model.entity;


import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.UserRole;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "user")
@Getter
//@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() where user_id = ?")
//@Where(clause = "deleted_at is NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String socialId;

    private String profileUrl;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id")
    private TokenEntity tokenEntity;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private Timestamp registerAt;

    private Timestamp updatedAt;

    @PrePersist
    void registedAt() {
        this.registerAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }


    @Builder(builderClassName = "SignUpDtoToEntity", builderMethodName = "SignUpDtoToEntity")
    public UserEntity (String socialId, SocialType socialType, String nickname, TokenEntity tokenEntity) {

        Assert.notNull(socialId, "socialId must not be null");
        Assert.notNull(socialType, "socialType must not be null");
        Assert.notNull(tokenEntity, "token must not be null");

        this.socialId = socialId;
        this.socialType = socialType;
        this.nickname = nickname;
        this.tokenEntity = tokenEntity;
    }

    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
