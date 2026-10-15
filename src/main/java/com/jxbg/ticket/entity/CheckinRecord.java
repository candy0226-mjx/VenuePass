package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("checkin_record")
@EqualsAndHashCode(callSuper = true)
public class CheckinRecord extends BaseEntity {

    @TableId
    private Long id;

    private Long codeId;

    private Long orderId;

    private Long checkerId;

    private String result;

    private String message;
}
