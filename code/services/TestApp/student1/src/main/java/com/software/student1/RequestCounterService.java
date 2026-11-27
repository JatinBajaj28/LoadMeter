package com.software.student1;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RequestCounterService {
    private static final String GET_COUNT_KEY = "student1:requests:get";
    private static final String POST_COUNT_KEY = "student1:requests:post";

    private final StringRedisTemplate redisTemplate;

    public RequestCounterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RequestCounts recordGetRequest() {
        increment(GET_COUNT_KEY);
        return getCounts();
    }

    public RequestCounts recordPostRequest() {
        increment(POST_COUNT_KEY);
        return getCounts();
    }

    public RequestCounts getCurrentCounts() {
        return getCounts();
    }

    private RequestCounts getCounts() {
        long getCount = readCount(GET_COUNT_KEY);
        long postCount = readCount(POST_COUNT_KEY);
        return new RequestCounts(getCount, postCount, getCount + postCount);
    }

    private long readCount(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Long.parseLong(value);
    }

    private void increment(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            throw new IllegalStateException("Redis did not return an updated request count");
        }
    }
}
