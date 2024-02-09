package com.dodal.meet.repository;


import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserEntityCacheRepository {

    private final RedisTemplate<String, Object> userEntityRedisTemplate;
    private final ObjectMapper objectMapper;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(30);

    public void setUserEntity(UserEntity userEntity) {
        String key = getKey(userEntity.getSocialId(), userEntity.getSocialType());

        String userEntityValue;
        try {
            userEntityValue = objectMapper.writeValueAsString(userEntity);
            log.info("Set UserEntity to Redis {}:{}", key, userEntityValue);
            userEntityRedisTemplate.opsForValue().setIfAbsent(key, userEntityValue, USER_CACHE_TTL);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new DodalApplicationException(ErrorCode.REDIS_JSON_PARSING_ERROR);
        }
    }

    public Optional<UserEntity> getUserEntity(String socialId, SocialType socialType) {
        String key = getKey(socialId, socialType);
        String raw = String.valueOf(userEntityRedisTemplate.opsForValue().get(key));
        try {
            if (!StringUtils.isEmpty(raw)) {
                UserEntity userEntity = objectMapper.readValue(raw, UserEntity.class);
                log.info("Get UserEntity from Redis {}, {}",key, userEntity);
                return Optional.ofNullable(userEntity);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new DodalApplicationException(ErrorCode.REDIS_JSON_PARSING_ERROR);
        }
        throw new DodalApplicationException(ErrorCode.REDIS_VALUE_NOT_FOUND);
    }

    private String getKey(String socialId, SocialType socialType) {
        return socialId + socialType +"ENTITY";
    }

    public void deleteUserEntity(String socialId, SocialType socialType) {
        String key = getKey(socialId, socialType);
        userEntityRedisTemplate.delete(key);
        log.info("Deleted UserEntity from Redis: {}", key);
    }
}
