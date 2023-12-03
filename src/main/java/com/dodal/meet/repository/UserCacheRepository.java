package com.dodal.meet.repository;


import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, Object> userRedisTemplate;
    private final ObjectMapper objectMapper;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(30);

    public void setUser(User user) {
        String key = getKey(user.getSocialId(), user.getSocialType());
        String userValue;
        try {
            userValue = objectMapper.writeValueAsString(user);
            log.info("Set user to Redis {}:{}", key, userValue);
            userRedisTemplate.opsForValue().setIfAbsent(key, userValue, USER_CACHE_TTL);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new DodalApplicationException(ErrorCode.REDIS_JSON_PARSING_ERROR);
        }
    }

    public Optional<User> getUser(String socialId, SocialType socialType) {
        String key = getKey(socialId, socialType);
        String raw = String.valueOf(userRedisTemplate.opsForValue().get(key));
        try {
            if (!StringUtils.isEmpty(raw)) {
                User user = objectMapper.readValue(raw, User.class);
                log.info("Get user from Redis {}, {}",key, user);
                return Optional.ofNullable(user);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new DodalApplicationException(ErrorCode.REDIS_JSON_PARSING_ERROR);
        }
        throw new DodalApplicationException(ErrorCode.REDIS_VALUE_NOT_FOUND);
    }

    private String getKey(String socialId, SocialType socialType) {
        return socialId + socialType;
    }
}
