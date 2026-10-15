package com.jxbg.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PayOrderRequest {

    @NotBlank(message = "支付流水号不能为空")
    @Size(max = 64, message = "支付流水号不能超过64个字符")
    private String paymentNo;
}
