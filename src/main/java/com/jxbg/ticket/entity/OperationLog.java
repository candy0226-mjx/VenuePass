package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("operation_log")
@EqualsAndHashCode(callSuper = true)
public class OperationLog extends BaseEntity {

    @TableId
    private Long id;

    private Long operatorId;

    private String operatorRole;

    private String operationType;

    private String businessType;

    private Long businessId;

    private String content;

    private String result;
}
