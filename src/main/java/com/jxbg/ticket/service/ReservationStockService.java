package com.jxbg.ticket.service;

import java.util.List;

public interface ReservationStockService {

    void tryReserve(Long sessionId, List<Long> seatIds);

    void release(Long sessionId, List<Long> seatIds);
}
