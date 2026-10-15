package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.entity.OrderItem;
import com.jxbg.ticket.entity.ReserveSession;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.service.OrderItemService;
import com.jxbg.ticket.service.ReservationStockService;
import com.jxbg.ticket.service.ReserveSessionService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationStockServiceImpl implements ReservationStockService {

    private static final String STOCK_KEY_PREFIX = "reserve:session:stock:";
    private static final String RESERVED_SEATS_KEY_PREFIX = "reserve:session:reserved-seats:";
    private static final String INIT_LOCK_KEY_PREFIX = "lock:reserve:session:init:";
    private static final long INIT_LOCK_WAIT_SECONDS = 1;
    private static final long INIT_LOCK_LEASE_SECONDS = 5;

    private static final Long RESERVE_SUCCESS = 1L;
    private static final Long SEAT_RESERVED = -1L;
    private static final Long STOCK_NOT_ENOUGH = -2L;

    private static final DefaultRedisScript<Long> RESERVE_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('EXISTS', KEYS[1]) == 0 then
                return -3
            end
            for i = 1, #ARGV do
                if redis.call('SISMEMBER', KEYS[2], ARGV[i]) == 1 then
                    return -1
                end
            end
            local stock = tonumber(redis.call('GET', KEYS[1]))
            if stock < #ARGV then
                return -2
            end
            redis.call('DECRBY', KEYS[1], #ARGV)
            for i = 1, #ARGV do
                redis.call('SADD', KEYS[2], ARGV[i])
            end
            return 1
            """, Long.class);

    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('EXISTS', KEYS[1]) == 0 then
                return 0
            end
            local removed = 0
            for i = 1, #ARGV do
                removed = removed + redis.call('SREM', KEYS[2], ARGV[i])
            end
            if removed > 0 then
                redis.call('INCRBY', KEYS[1], removed)
            end
            return removed
            """, Long.class);

    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final ReserveSessionService reserveSessionService;
    private final OrderItemService orderItemService;

    @Override
    public void tryReserve(Long sessionId, List<Long> seatIds) {
        ensureStockCache(sessionId);
        Long result = stringRedisTemplate.execute(RESERVE_SCRIPT, buildKeys(sessionId), toArgs(seatIds));
        if (RESERVE_SUCCESS.equals(result)) {
            return;
        }
        if (SEAT_RESERVED.equals(result)) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
        if (STOCK_NOT_ENOUGH.equals(result)) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED, "当前场次可预约资源不足");
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "预约库存缓存未初始化");
    }

    @Override
    public void release(Long sessionId, List<Long> seatIds) {
        stringRedisTemplate.execute(RELEASE_SCRIPT, buildKeys(sessionId), toArgs(seatIds));
    }

    private void ensureStockCache(Long sessionId) {
        String stockKey = buildStockKey(sessionId);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey))) {
            return;
        }

        RLock lock = redissonClient.getLock(INIT_LOCK_KEY_PREFIX + sessionId);
        boolean locked = false;
        try {
            locked = lock.tryLock(INIT_LOCK_WAIT_SECONDS, INIT_LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "场次库存初始化中，请稍后重试");
            }
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey))) {
                return;
            }

            ReserveSession session = reserveSessionService.getById(sessionId);
            if (session == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "场次不存在");
            }
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(session.getAvailableStock()));

            List<String> reservedSeatIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getSessionId, sessionId)
                            .eq(OrderItem::getActiveFlag, 1))
                    .stream()
                    .map(OrderItem::getSeatId)
                    .map(String::valueOf)
                    .toList();
            if (!reservedSeatIds.isEmpty()) {
                stringRedisTemplate.opsForSet().add(buildReservedSeatsKey(sessionId), reservedSeatIds.toArray(String[]::new));
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化场次库存被中断");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private List<String> buildKeys(Long sessionId) {
        return List.of(buildStockKey(sessionId), buildReservedSeatsKey(sessionId));
    }

    private String buildStockKey(Long sessionId) {
        return STOCK_KEY_PREFIX + sessionId;
    }

    private String buildReservedSeatsKey(Long sessionId) {
        return RESERVED_SEATS_KEY_PREFIX + sessionId;
    }

    private String[] toArgs(List<Long> seatIds) {
        List<String> args = new ArrayList<>(seatIds.size());
        for (Long seatId : seatIds) {
            args.add(String.valueOf(seatId));
        }
        return args.toArray(String[]::new);
    }
}
