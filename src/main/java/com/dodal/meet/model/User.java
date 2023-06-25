package com.dodal.meet.model;


import com.dodal.meet.model.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
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
@Builder
public class User {

    private Long id;
    private String email;
    private String nickname;
    private String socialId;
    private String profileUrl;
    private UserRole role;
    private SocialType socialType;
    private Timestamp registerAt;
    private Timestamp updatedAt;

    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getSocialId(),
                entity.getProfileUrl(),
                entity.getRole(),
                entity.getSocialType(),
                entity.getRegisterAt(),
                entity.getUpdatedAt()
        );

    }
}
