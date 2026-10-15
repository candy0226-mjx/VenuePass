package com.jxbg.ticket.mq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "ticket.mq.order-timeout", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoopOrderTimeoutMessageProducer implements OrderTimeoutMessageProducer {

    @Override
    public void sendCloseOrderMessage(Long orderId) {
    }
}
