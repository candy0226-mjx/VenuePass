package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@TableName("seat")
@EqualsAndHashCode(callSuper = true)
public class Seat extends BaseEntity {

    @TableId
    private Long id;

    private Long venueId;

    private Long areaId;

    private String seatNo;

    private String rowNo;

    private String colNo;

    private String name;

    private BigDecimal basePrice;

    private String resourceType;

    private Integer status;
}
