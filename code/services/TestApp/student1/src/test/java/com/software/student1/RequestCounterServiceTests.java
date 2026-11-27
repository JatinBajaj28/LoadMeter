package com.software.student1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestCounterServiceTests {
    private static final String GET_COUNT_KEY = "student1:requests:get";
    private static final String POST_COUNT_KEY = "student1:requests:post";

    private final StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    private final RequestCounterService requestCounterService = new RequestCounterService(redisTemplate);

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void recordGetRequestIncrementsGetCountAndReturnsTotals() {
        when(valueOperations.increment(GET_COUNT_KEY)).thenReturn(1L);
        when(valueOperations.get(GET_COUNT_KEY)).thenReturn("1");
        when(valueOperations.get(POST_COUNT_KEY)).thenReturn(null);

        RequestCounts counts = requestCounterService.recordGetRequest();

        assertEquals(new RequestCounts(1, 0, 1), counts);
        verify(valueOperations).increment(GET_COUNT_KEY);
    }

    @Test
    void recordPostRequestIncrementsPostCountAndReturnsTotals() {
        when(valueOperations.increment(POST_COUNT_KEY)).thenReturn(1L);
        when(valueOperations.get(GET_COUNT_KEY)).thenReturn("2");
        when(valueOperations.get(POST_COUNT_KEY)).thenReturn("1");

        RequestCounts counts = requestCounterService.recordPostRequest();

        assertEquals(new RequestCounts(2, 1, 3), counts);
        verify(valueOperations).increment(POST_COUNT_KEY);
    }

    @Test
    void recordRequestFailsIfRedisDoesNotReturnAnIncrementedCount() {
        when(valueOperations.increment(GET_COUNT_KEY)).thenReturn(null);

        assertThrows(IllegalStateException.class, requestCounterService::recordGetRequest);
    }

    @Test
    void getCurrentCountsReturnsCountsWithoutIncrementingThem() {
        when(valueOperations.get(GET_COUNT_KEY)).thenReturn("4");
        when(valueOperations.get(POST_COUNT_KEY)).thenReturn("2");

        RequestCounts counts = requestCounterService.getCurrentCounts();

        assertEquals(new RequestCounts(4, 2, 6), counts);
        verify(valueOperations, never()).increment(GET_COUNT_KEY);
        verify(valueOperations, never()).increment(POST_COUNT_KEY);
    }
}
