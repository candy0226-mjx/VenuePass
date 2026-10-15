package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@TableName("order_item")
@EqualsAndHashCode(callSuper = true)
public class OrderItem extends BaseEntity {

    @TableId
    private Long id;

    private Long orderId;

    private Long sessionId;

    private Long seatId;

    private String seatNo;

    private BigDecimal price;

    private Integer activeFlag;
}
