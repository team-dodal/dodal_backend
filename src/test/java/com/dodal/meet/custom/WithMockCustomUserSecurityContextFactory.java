package com.dodal.meet.custom;

import com.dodal.meet.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User principal = new User(customUser.id(), customUser.email(), customUser.nickname(), customUser.socialId(),
                null, customUser.userRole(), customUser.socialType(), null, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        context.setAuthentication(authentication);

        return context;
    }
}
