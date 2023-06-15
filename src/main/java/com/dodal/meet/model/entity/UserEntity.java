package com.dodal.meet.model.entity;


import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "user")
@Getter
@Setter
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() where id = ?")
@Where(clause = "deleted_at is NULL")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String nickname;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "social_type")
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(name = "registered_at")
    private Timestamp registerAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registedAt() {
        this.registerAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static UserEntity of(String email, SocialType socialType, String refreshToken) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setSocialType(socialType);
        userEntity.setRefreshToken(refreshToken);
        return userEntity;
    }
}
