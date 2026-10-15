package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("session")
@EqualsAndHashCode(callSuper = true)
public class ReserveSession extends BaseEntity {

    @TableId
    private Long id;

    private Long activityId;

    private Long venueId;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer totalStock;

    private Integer availableStock;

    private Integer status;
}
