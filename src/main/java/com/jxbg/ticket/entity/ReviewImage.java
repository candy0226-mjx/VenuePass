package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("review_image")
@EqualsAndHashCode(callSuper = true)
public class ReviewImage extends BaseEntity {

    @TableId
    private Long id;

    private Long reviewId;

    private String imageUrl;

    private Integer sort;
}
