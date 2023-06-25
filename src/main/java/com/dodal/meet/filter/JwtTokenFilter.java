package com.dodal.meet.filter;

import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.dodal.meet.service.UserService;
import com.dodal.meet.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // header validation
        final String token;
        try {
            final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header == null || !header.startsWith("Bearer ")) {
                log.error("Request header is null or invalid {}", request.getRequestURL());
                filterChain.doFilter(request, response);
                return;
            }
            token = header.split(" ")[1].trim();

            // check token is valid
            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            // get socialId, socialType from token
            final String socialId = JwtTokenUtils.getUserSocialId(token, key);
            final SocialType socialType = getSocialType(JwtTokenUtils.getUserSocialType(token, key));

            // check the user is valid
            User user = userService.findBySocialIdAndSocialTypeToUser(socialId, socialType);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, null
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private SocialType getSocialType(String type) {
        for (SocialType st : SocialType.values()) {
            if(st.name().equals(type)) {
                return st;
            }
        }
        return null;
    }

}
