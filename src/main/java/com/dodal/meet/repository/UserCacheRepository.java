package com.dodal.meet.repository;


import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;

    private final static Duration USER_CACHE_TTL = Duration.ofDays(30);

    public void setUser(User user) {
        String key = getKey(user.getSocialId(), user.getSocialType());
        log.info("Set User to Redis {}:{}", key, user);
        userRedisTemplate.opsForValue().setIfAbsent(getKey(user.getSocialId(), user.getSocialType()), user, USER_CACHE_TTL);
    }

    public Optional<User> getUser(String socialId, SocialType socialType) {
        String key = getKey(socialId, socialType);
        User user = userRedisTemplate.opsForValue().get(key);
        log.info("Get data from Redis {}, {}",key, user);
        return Optional.ofNullable(user);
    }

    private String getKey(String socialId, SocialType socialType) {
        return socialId + socialType;
    }
}
