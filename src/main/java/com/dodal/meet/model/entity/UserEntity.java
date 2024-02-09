package com.dodal.meet.model.entity;


import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.model.BaseTime;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
public class UserEntity extends BaseTime {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 30)
    private String email;

    @Column(nullable = false, length = 16)
    private String nickname;

    @Column(nullable = false, length = 200)
    private String socialId;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = true, length = 255)
    private String profileUrl;

    @Column(nullable = true, length = 50)
    private String content;

    @Column(nullable = false, length = 1)
    private char alarmYn;

    @Column(nullable = false, length = 30)
    private int accuseCnt;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "token_id", nullable = true)
    private TokenEntity tokenEntity;


    @PrePersist
    void registeredAt() {
        this.role = UserRole.USER;
        this.alarmYn = 'Y';
        this.accuseCnt = 0;
    }

    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
    public void updateNickname(String nickname) {this.nickname = nickname;}

    public void updateContent(String content) {this.content = content;}

    public void updateAccuseCnt(int num) {
        this.accuseCnt += num;
    }

    public static UserEntity newInstance(UserSignUpRequest userSignUpRequest, TokenEntity tokenEntity) {
        return UserEntity.builder()
                .socialId(userSignUpRequest.getSocialId())
                .socialType(userSignUpRequest.getSocialType())
                .email(userSignUpRequest.getEmail())
                .nickname(userSignUpRequest.getNickname())
                .profileUrl(userSignUpRequest.getProfileUrl())
                .content(userSignUpRequest.getContent())
                .tokenEntity(tokenEntity)
                .build();
    }
}
