package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("checkin_code")
@EqualsAndHashCode(callSuper = true)
public class CheckinCode extends BaseEntity {

    @TableId
    private Long id;

    private Long orderId;

    private String code;

    private String status;

    private LocalDateTime expiredAt;

    private LocalDateTime usedAt;
}
