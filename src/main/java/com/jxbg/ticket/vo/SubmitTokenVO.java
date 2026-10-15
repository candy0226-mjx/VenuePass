package com.jxbg.ticket.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmitTokenVO {

    private String submitToken;

    private Long expireSeconds;
}
