package com.dodal.meet.custom;


import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    long id() default 1L;
    String email() default "sasca37@naver.com";
    String nickname() default "sasca37";
    String socialId() default "999999999";
    UserRole userRole() default UserRole.USER;
    SocialType socialType() default SocialType.KAKAO;
}
