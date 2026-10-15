package com.jxbg.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckinVerifyRequest {

    @NotBlank(message = "核销码不能为空")
    @Size(max = 64, message = "核销码不能超过64个字符")
    private String code;

    @NotNull(message = "核销员ID不能为空")
    @Positive(message = "核销员ID必须大于0")
    private Long checkerId;
}
