package com.jxbg.ticket.service.impl;

import com.jxbg.ticket.entity.ReserveSession;
import com.jxbg.ticket.entity.Seat;
import com.jxbg.ticket.entity.Venue;
import com.jxbg.ticket.mapper.VenueMapper;
import com.jxbg.ticket.service.HotStatsService;
import com.jxbg.ticket.service.ReserveSessionService;
import com.jxbg.ticket.service.SeatService;
import com.jxbg.ticket.vo.SeatHotVO;
import com.jxbg.ticket.vo.VenueHotVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotStatsServiceImpl implements HotStatsService {

    private static final String VENUE_HOT_KEY = "rank:venue:hot";
    private static final String SEAT_HOT_KEY = "rank:seat:hot";
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;
    private static final double VENUE_VIEW_SCORE = 1;
    private static final double ORDER_CREATED_SCORE = 5;
    private static final double ORDER_PAID_SCORE = 10;
    private static final double ORDER_CHECKED_IN_SCORE = 8;
    private static final double REVIEW_CREATED_SCORE = 6;

    private final StringRedisTemplate stringRedisTemplate;
    private final VenueMapper venueMapper;
    private final SeatService seatService;
    private final ReserveSessionService reserveSessionService;

    @Override
    public void recordVenueView(Long venueId) {
        incrementVenue(venueId, VENUE_VIEW_SCORE);
    }

    @Override
    public void recordOrderCreated(Long sessionId, List<Long> seatIds) {
        incrementBySession(sessionId, ORDER_CREATED_SCORE);
        incrementSeats(seatIds, ORDER_CREATED_SCORE);
    }

    @Override
    public void recordOrderPaid(Long sessionId, List<Long> seatIds) {
        incrementBySession(sessionId, ORDER_PAID_SCORE);
        incrementSeats(seatIds, ORDER_PAID_SCORE);
    }

    @Override
    public void recordOrderCheckedIn(Long sessionId, List<Long> seatIds) {
        incrementBySession(sessionId, ORDER_CHECKED_IN_SCORE);
        incrementSeats(seatIds, ORDER_CHECKED_IN_SCORE);
    }

    @Override
    public void recordReviewCreated(Long venueId) {
        incrementVenue(venueId, REVIEW_CREATED_SCORE);
    }

    @Override
    public List<VenueHotVO> listHotVenues(Integer limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples = reverseRangeWithScores(VENUE_HOT_KEY, limit);
        if (tuples.isEmpty()) {
            return List.of();
        }

        List<Long> venueIds = tuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
        Map<Long, Venue> venueMap = venueMapper.selectBatchIds(venueIds).stream()
                .collect(Collectors.toMap(Venue::getId, venue -> venue));

        return tuples.stream()
                .map(tuple -> buildVenueHotVO(tuple, venueMap))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<SeatHotVO> listHotSeats(Integer limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples = reverseRangeWithScores(SEAT_HOT_KEY, limit);
        if (tuples.isEmpty()) {
            return List.of();
        }

        List<Long> seatIds = tuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
        Map<Long, Seat> seatMap = seatService.listByIds(seatIds).stream()
                .collect(Collectors.toMap(Seat::getId, seat -> seat));
        Map<Long, Venue> venueMap = venueMapper.selectBatchIds(seatMap.values().stream()
                        .map(Seat::getVenueId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(Venue::getId, venue -> venue));

        return tuples.stream()
                .map(tuple -> buildSeatHotVO(tuple, seatMap, venueMap))
                .filter(Objects::nonNull)
                .toList();
    }

    private void incrementBySession(Long sessionId, double score) {
        ReserveSession session = reserveSessionService.getById(sessionId);
        if (session != null) {
            incrementVenue(session.getVenueId(), score);
        }
    }

    private void incrementVenue(Long venueId, double score) {
        if (venueId != null) {
            stringRedisTemplate.opsForZSet().incrementScore(VENUE_HOT_KEY, String.valueOf(venueId), score);
        }
    }

    private void incrementSeats(List<Long> seatIds, double score) {
        if (seatIds == null || seatIds.isEmpty()) {
            return;
        }
        seatIds.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .forEach(seatId -> stringRedisTemplate.opsForZSet().incrementScore(SEAT_HOT_KEY, seatId, score));
    }

    private Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, Integer limit) {
        int safeLimit = normalizeLimit(limit);
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, safeLimit - 1L);
        return tuples == null ? Set.of() : tuples;
    }

    private VenueHotVO buildVenueHotVO(ZSetOperations.TypedTuple<String> tuple, Map<Long, Venue> venueMap) {
        String venueIdText = tuple.getValue();
        if (venueIdText == null) {
            return null;
        }
        Venue venue = venueMap.get(Long.valueOf(venueIdText));
        if (venue == null) {
            return null;
        }
        return VenueHotVO.of(venue, scoreAsLong(tuple));
    }

    private SeatHotVO buildSeatHotVO(ZSetOperations.TypedTuple<String> tuple, Map<Long, Seat> seatMap, Map<Long, Venue> venueMap) {
        String seatIdText = tuple.getValue();
        if (seatIdText == null) {
            return null;
        }
        Seat seat = seatMap.get(Long.valueOf(seatIdText));
        if (seat == null) {
            return null;
        }
        return SeatHotVO.of(seat, venueMap.get(seat.getVenueId()), scoreAsLong(tuple));
    }

    private Long scoreAsLong(ZSetOperations.TypedTuple<String> tuple) {
        Double score = tuple.getScore();
        return score == null ? 0L : score.longValue();
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
