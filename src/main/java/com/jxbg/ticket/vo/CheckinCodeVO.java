package com.jxbg.ticket.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckinCodeVO {

    private Long orderId;

    private String code;

    private String status;

    private LocalDateTime expiredAt;
}
