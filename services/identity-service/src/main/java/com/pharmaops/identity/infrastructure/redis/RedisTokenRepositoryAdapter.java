package com.pharmaops.identity.infrastructure.redis;

import com.pharmaops.identity.application.port.out.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisTokenRepositoryAdapter implements TokenRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(UUID userId, String token, Duration ttl) {
        redisTemplate.opsForValue().set(key(userId), token, ttl);
    }

    @Override
    public Optional<String> find(UUID userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(userId)));
    }

    @Override
    public void delete(UUID userId) {
        redisTemplate.delete(key(userId));
    }

    @Override
    public boolean isValid(UUID userId, String token) {
        String stored = redisTemplate.opsForValue().get(key(userId));
        return token.equals(stored);
    }

    private String key(UUID userId) {
        return "refresh:" + userId;
    }
}
