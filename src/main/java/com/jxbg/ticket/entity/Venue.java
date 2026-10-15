package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("venue")
@EqualsAndHashCode(callSuper = true)
public class Venue extends BaseEntity {

    @TableId
    private Long id;

    private String name;

    private String address;

    private String description;

    private String contactPhone;

    private Integer status;
}
