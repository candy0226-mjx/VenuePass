package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("review_like")
@EqualsAndHashCode(callSuper = true)
public class ReviewLike extends BaseEntity {

    @TableId
    private Long id;

    private Long reviewId;

    private Long userId;

    private Integer activeFlag;
}
