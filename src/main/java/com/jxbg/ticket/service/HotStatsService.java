package com.jxbg.ticket.service;

import com.jxbg.ticket.vo.SeatHotVO;
import com.jxbg.ticket.vo.VenueHotVO;

import java.util.List;

public interface HotStatsService {

    void recordVenueView(Long venueId);

    void recordOrderCreated(Long sessionId, List<Long> seatIds);

    void recordOrderPaid(Long sessionId, List<Long> seatIds);

    void recordOrderCheckedIn(Long sessionId, List<Long> seatIds);

    void recordReviewCreated(Long venueId);

    List<VenueHotVO> listHotVenues(Integer limit);

    List<SeatHotVO> listHotSeats(Integer limit);
}
