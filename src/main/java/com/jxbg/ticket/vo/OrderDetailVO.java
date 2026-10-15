package com.jxbg.ticket.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long activityId;

    private Long sessionId;

    private BigDecimal totalAmount;

    private String status;

    private LocalDateTime expireTime;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    private List<OrderItemVO> items;
}
