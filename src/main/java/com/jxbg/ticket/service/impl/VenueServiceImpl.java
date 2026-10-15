package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxbg.ticket.entity.Venue;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.mapper.VenueMapper;
import com.jxbg.ticket.service.HotStatsService;
import com.jxbg.ticket.service.VenueService;
import com.jxbg.ticket.vo.VenueBrowseHistoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VenueServiceImpl extends ServiceImpl<VenueMapper, Venue> implements VenueService {

    private static final String VENUE_DETAIL_KEY_PREFIX = "cache:venue:detail:";
    private static final String VENUE_DETAIL_LOCK_KEY_PREFIX = "lock:cache:venue:detail:";
    private static final String VENUE_HISTORY_KEY_PREFIX = "history:venue:";
    private static final String CACHE_NULL_VALUE = "";
    private static final Duration VENUE_DETAIL_CACHE_TTL = Duration.ofMinutes(30);
    private static final Duration VENUE_DETAIL_CACHE_TTL_RANDOM_RANGE = Duration.ofMinutes(5);
    private static final Duration CACHE_NULL_TTL = Duration.ofMinutes(2);
    private static final Duration CACHE_NULL_TTL_RANDOM_RANGE = Duration.ofSeconds(30);
    private static final int DEFAULT_HOT_LIMIT = 10;
    private static final int DEFAULT_HISTORY_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    private static final long CACHE_REBUILD_LOCK_WAIT_SECONDS = 1;
    private static final long CACHE_REBUILD_LOCK_LEASE_SECONDS = 5;
    private static final long CACHE_RETRY_SLEEP_MILLIS = 50;
    private static final Random RANDOM = new Random();

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final HotStatsService hotStatsService;

    @Override
    public Venue getVenueDetail(Long id) {
        String key = buildVenueDetailKey(id);
        String cacheValue = stringRedisTemplate.opsForValue().get(key);
        if (cacheValue != null) {
            if (CACHE_NULL_VALUE.equals(cacheValue)) {
                return null;
            }
            return readVenue(cacheValue);
        }

        return rebuildVenueDetailCacheWithMutex(id, key);
    }

    private Venue rebuildVenueDetailCacheWithMutex(Long id, String key) {
        RLock lock = redissonClient.getLock(buildVenueDetailLockKey(id));
        boolean locked = false;
        try {
            locked = lock.tryLock(CACHE_REBUILD_LOCK_WAIT_SECONDS, CACHE_REBUILD_LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                sleepBeforeRetry();
                return getVenueDetail(id);
            }

            String cacheValue = stringRedisTemplate.opsForValue().get(key);
            if (cacheValue != null) {
                return CACHE_NULL_VALUE.equals(cacheValue) ? null : readVenue(cacheValue);
            }

            Venue venue = getById(id);
            if (venue == null) {
                stringRedisTemplate.opsForValue().set(key, CACHE_NULL_VALUE, randomTtl(CACHE_NULL_TTL, CACHE_NULL_TTL_RANDOM_RANGE));
                return null;
            }

            stringRedisTemplate.opsForValue().set(key, writeVenue(venue), randomTtl(VENUE_DETAIL_CACHE_TTL, VENUE_DETAIL_CACHE_TTL_RANDOM_RANGE));
            return venue;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("rebuild venue cache interrupted, venueId={}", id, ex);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取场馆缓存锁被中断");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void sleepBeforeRetry() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(CACHE_RETRY_SLEEP_MILLIS);
    }

    private Duration randomTtl(Duration baseTtl, Duration randomRange) {
        long randomSeconds = randomRange.isZero() ? 0 : RANDOM.nextLong(randomRange.toSeconds() + 1);
        return baseTtl.plusSeconds(randomSeconds);
    }

    private String buildVenueDetailLockKey(Long id) {
        return VENUE_DETAIL_LOCK_KEY_PREFIX + id;
    }

    @Override
    public Venue getVenueDetail(Long userId, Long id) {
        Venue venue = getVenueDetail(id);
        if (venue != null && venue.getStatus() != null && venue.getStatus() == 1) {
            recordVenueBrowse(userId, venue.getId());
        }
        return venue;
    }

    @Override
    public List<VenueBrowseHistoryVO> listBrowseHistory(Long userId, Integer limit) {
        int safeLimit = normalizeLimit(limit, DEFAULT_HISTORY_LIMIT);
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(buildVenueHistoryKey(userId), 0, safeLimit - 1L);
        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        List<Long> venueIds = tuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
        Map<Long, Venue> venueMap = listByIds(venueIds).stream()
                .collect(Collectors.toMap(Venue::getId, venue -> venue));

        return tuples.stream()
                .map(tuple -> buildBrowseHistoryVO(tuple, venueMap))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Long createVenue(Venue venue) {
        save(venue);
        return venue.getId();
    }

    @Override
    public void updateVenue(Long id, Venue venue) {
        venue.setId(id);
        updateById(venue);
        deleteVenueDetailCache(id);
    }

    @Override
    public void disableVenue(Long id) {
        Venue venue = new Venue();
        venue.setId(id);
        venue.setStatus(0);
        updateById(venue);
        deleteVenueDetailCache(id);
    }

    private void deleteVenueDetailCache(Long id) {
        stringRedisTemplate.delete(buildVenueDetailKey(id));
    }

    private void recordVenueBrowse(Long userId, Long venueId) {
        String venueIdText = String.valueOf(venueId);
        hotStatsService.recordVenueView(venueId);
        if (userId != null) {
            stringRedisTemplate.opsForZSet().add(buildVenueHistoryKey(userId), venueIdText, System.currentTimeMillis());
        }
    }

    private VenueBrowseHistoryVO buildBrowseHistoryVO(ZSetOperations.TypedTuple<String> tuple, Map<Long, Venue> venueMap) {
        String venueIdText = tuple.getValue();
        if (venueIdText == null) {
            return null;
        }
        Venue venue = venueMap.get(Long.valueOf(venueIdText));
        if (venue == null) {
            return null;
        }
        return VenueBrowseHistoryVO.of(venue, tuple.getScore());
    }

    private int normalizeLimit(Integer limit, int defaultLimit) {
        if (limit == null || limit < 1) {
            return defaultLimit;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private String buildVenueDetailKey(Long id) {
        return VENUE_DETAIL_KEY_PREFIX + id;
    }

    private String buildVenueHistoryKey(Long userId) {
        return VENUE_HISTORY_KEY_PREFIX + userId;
    }

    private Venue readVenue(String cacheValue) {
        try {
            return objectMapper.readValue(cacheValue, Venue.class);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "场馆缓存数据解析失败");
        }
    }

    private String writeVenue(Venue venue) {
        try {
            return objectMapper.writeValueAsString(venue);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "场馆缓存数据写入失败");
        }
    }
}
