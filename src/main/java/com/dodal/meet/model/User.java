package com.dodal.meet.model;


import com.dodal.meet.model.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
@Getter
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements OAuth2User {

    private Integer id;
    private String email;
    private String nickname;
    private String profileUrl;
    private UserRole role;
    private SocialType socialType;
    private Timestamp registerAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getProfileUrl(),
                entity.getRole(),
                entity.getSocialType(),
                entity.getRegisterAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );

    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return this.nickname;
    }
}
