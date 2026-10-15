package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("review")
@EqualsAndHashCode(callSuper = true)
public class Review extends BaseEntity {

    @TableId
    private Long id;

    private Long userId;

    private Long orderId;

    private Long venueId;

    private Long activityId;

    private Long sessionId;

    private Integer rating;

    private String content;

    private Integer likeCount;

    private Integer imageCount;

    private Integer status;

    private Integer activeFlag;
}
