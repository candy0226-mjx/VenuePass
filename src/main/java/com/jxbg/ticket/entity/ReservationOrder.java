package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("reservation_order")
@EqualsAndHashCode(callSuper = true)
public class ReservationOrder extends BaseEntity {

    @TableId
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
}
