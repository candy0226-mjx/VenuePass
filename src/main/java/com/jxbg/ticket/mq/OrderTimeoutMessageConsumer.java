package com.jxbg.ticket.mq;

import com.jxbg.ticket.service.ReservationOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ticket.mq.order-timeout", name = "enabled", havingValue = "true")
public class OrderTimeoutMessageConsumer {

    private final ReservationOrderService reservationOrderService;

    @RabbitListener(queues = OrderTimeoutMqConstants.ORDER_CLOSE_QUEUE)
    public void closeTimeoutOrder(String orderId) {
        Long parsedOrderId;
        try {
            parsedOrderId = Long.valueOf(orderId);
        } catch (NumberFormatException ex) {
            log.warn("invalid order timeout message, orderId={}", orderId);
            return;
        }
        boolean closed = reservationOrderService.closeTimeoutOrder(parsedOrderId);
        log.info("consumed order timeout message, orderId={}, closed={}", parsedOrderId, closed);
    }
}
