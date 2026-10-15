package com.jxbg.ticket.mq;

public interface OrderTimeoutMessageProducer {

    void sendCloseOrderMessage(Long orderId);
}
