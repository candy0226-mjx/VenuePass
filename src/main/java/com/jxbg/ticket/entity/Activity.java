package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("activity")
@EqualsAndHashCode(callSuper = true)
public class Activity extends BaseEntity {

    @TableId
    private Long id;

    private Long venueId;

    private String name;

    private String description;

    private String posterUrl;

    private Integer status;
}
