package com.jxbg.ticket.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long seatId;

    private String seatNo;

    private BigDecimal price;
}
