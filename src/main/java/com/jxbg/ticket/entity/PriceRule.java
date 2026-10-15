package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@TableName("price_rule")
@EqualsAndHashCode(callSuper = true)
public class PriceRule extends BaseEntity {

    @TableId
    private Long id;

    private Long activityId;

    private Long sessionId;

    private Long areaId;

    private BigDecimal price;

    private Integer status;
}
