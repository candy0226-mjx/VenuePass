package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_record")
@EqualsAndHashCode(callSuper = true)
public class PaymentRecord extends BaseEntity {

    @TableId
    private Long id;

    private Long orderId;

    private String orderNo;

    private String paymentNo;

    private BigDecimal amount;

    private String status;

    private LocalDateTime paidAt;
}
