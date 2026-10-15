package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("venue_area")
@EqualsAndHashCode(callSuper = true)
public class VenueArea extends BaseEntity {

    @TableId
    private Long id;

    private Long venueId;

    private String name;

    private String type;

    private Integer capacity;

    private Integer sort;

    private Integer status;
}
