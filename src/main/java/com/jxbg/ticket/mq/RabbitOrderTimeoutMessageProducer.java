package com.jxbg.ticket.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ticket.mq.order-timeout", name = "enabled", havingValue = "true")
public class RabbitOrderTimeoutMessageProducer implements OrderTimeoutMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendCloseOrderMessage(Long orderId) {
        rabbitTemplate.convertAndSend(OrderTimeoutMqConstants.ORDER_DELAY_EXCHANGE,
                OrderTimeoutMqConstants.ORDER_DELAY_ROUTING_KEY, String.valueOf(orderId));
        log.info("sent order timeout delay message, orderId={}", orderId);
    }
}
