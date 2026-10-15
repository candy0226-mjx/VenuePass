package com.jxbg.ticket.config;

import com.jxbg.ticket.mq.OrderTimeoutMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "ticket.mq.order-timeout", name = "enabled", havingValue = "true")
public class OrderTimeoutRabbitConfig {

    @Value("${ticket.order.close-delay-ms:900000}")
    private long closeDelayMs;

    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(OrderTimeoutMqConstants.ORDER_DELAY_EXCHANGE);
    }

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(OrderTimeoutMqConstants.ORDER_DELAY_QUEUE)
                .ttl((int) closeDelayMs)
                .deadLetterExchange(OrderTimeoutMqConstants.ORDER_CLOSE_EXCHANGE)
                .deadLetterRoutingKey(OrderTimeoutMqConstants.ORDER_CLOSE_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderDelayExchange())
                .with(OrderTimeoutMqConstants.ORDER_DELAY_ROUTING_KEY);
    }

    @Bean
    public DirectExchange orderCloseExchange() {
        return new DirectExchange(OrderTimeoutMqConstants.ORDER_CLOSE_EXCHANGE);
    }

    @Bean
    public Queue orderCloseQueue() {
        return QueueBuilder.durable(OrderTimeoutMqConstants.ORDER_CLOSE_QUEUE).build();
    }

    @Bean
    public Binding orderCloseBinding() {
        return BindingBuilder.bind(orderCloseQueue())
                .to(orderCloseExchange())
                .with(OrderTimeoutMqConstants.ORDER_CLOSE_ROUTING_KEY);
    }
}
