package com.jxbg.ticket.job;

import com.jxbg.ticket.service.ReservationOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutJob {

    private final ReservationOrderService reservationOrderService;

    @Value("${ticket.order.timeout-batch-size:50}")
    private int batchSize;

    @Scheduled(cron = "${ticket.order.timeout-scan-cron:0 * * * * ?}")
    public void closeTimeoutOrders() {
        int count = reservationOrderService.closeTimeoutOrders(batchSize);
        if (count > 0) {
            log.info("closed timeout reservation orders, count={}", count);
        }
    }
}
