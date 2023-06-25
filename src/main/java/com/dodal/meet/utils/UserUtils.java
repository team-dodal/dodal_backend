package com.dodal.meet.utils;

import com.dodal.meet.model.User;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static User getUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return User.builder()
                .socialType(user.getSocialType())
                .socialId(user.getSocialId())
                .build();
    }
}
