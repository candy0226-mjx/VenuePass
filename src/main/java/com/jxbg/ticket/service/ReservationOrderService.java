package com.jxbg.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxbg.ticket.dto.CreateOrderRequest;
import com.jxbg.ticket.dto.PayOrderRequest;
import com.jxbg.ticket.entity.ReservationOrder;
import com.jxbg.ticket.vo.CheckinCodeVO;
import com.jxbg.ticket.vo.OrderDetailVO;

public interface ReservationOrderService extends IService<ReservationOrder> {

    String generateSubmitToken();

    Long createOrder(CreateOrderRequest request);

    OrderDetailVO getOrderDetail(Long id);

    void cancelOrder(Long id);

    CheckinCodeVO payOrder(Long id, PayOrderRequest request);

    int closeTimeoutOrders(int batchSize);

    boolean closeTimeoutOrder(Long id);
}
