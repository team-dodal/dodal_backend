package com.dodal.meet.model.entity;


import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.UserRole;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.util.Assert;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String socialId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String profileUrl;

    private String content;

    private char alarmYn;

    private int accuseCnt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "token_id")
    private TokenEntity tokenEntity;

    private Timestamp registerAt;

    private Timestamp updatedAt;

    @PrePersist
    void registedAt() {
        this.registerAt = Timestamp.from(Instant.now());
        this.role = UserRole.USER;
        this.alarmYn = 'Y';
        this.accuseCnt = 0;
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }


    @Builder(builderClassName = "SignUpDtoToEntity", builderMethodName = "SignUpDtoToEntity")
    public UserEntity (UserSignUpRequest request, TokenEntity tokenEntity) {

        Assert.notNull(request.getSocialId(), "socialId must not be null");
        Assert.notNull(request.getSocialType(), "socialType must not be null");
        Assert.notNull(tokenEntity, "token must not be null");

        this.socialId = request.getSocialId();
        this.socialType = request.getSocialType();
        this.email = request.getEmail();
        this.nickname = request.getNickname();
        this.profileUrl = request.getProfileUrl();
        this.content = request.getContent();
        this.tokenEntity = tokenEntity;
    }

    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
    public void updateNickname(String nickname) {this.nickname = nickname;}

    public void updateContent(String content) {this.content = content;}

    public void updateAccuseCnt(int num) {
        this.accuseCnt += num;
    }
}
