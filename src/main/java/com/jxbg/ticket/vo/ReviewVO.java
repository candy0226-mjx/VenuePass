package com.jxbg.ticket.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewVO {

    private Long id;

    private Long userId;

    private String username;

    private String nickname;

    private Long orderId;

    private Long venueId;

    private Long activityId;

    private Long sessionId;

    private Integer rating;

    private String content;

    private Integer status;

    private String statusName;

    private Integer likeCount;

    private Integer imageCount;

    private List<String> imageUrls;

    private LocalDateTime createTime;
}
