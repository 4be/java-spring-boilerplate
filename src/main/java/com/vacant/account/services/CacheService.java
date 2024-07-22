package com.vacant.account.services;

import com.vacant.account.utils.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {


    private final RedisTemplate<String, Object> redisTemplate;


    public void save(String key, Object value, Duration timeout) {
        if (ObjectUtils.isEmpty(timeout)) {
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, timeout);
        }
    }


    public <T> T get(String key, Class<T> type) {
        var value = redisTemplate.opsForValue().get(key);
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }

        return JsonMapper.objectToOther(value, type);
    }

}
