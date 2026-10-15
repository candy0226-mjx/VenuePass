package com.jxbg.ticket.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须大于0")
    private Long userId;

    @NotNull(message = "场次ID不能为空")
    @Positive(message = "场次ID必须大于0")
    private Long sessionId;

    @NotEmpty(message = "座位不能为空")
    private List<@NotNull(message = "座位ID不能为空") @Positive(message = "座位ID必须大于0") Long> seatIds;

    @NotBlank(message = "下单令牌不能为空")
    private String submitToken;
}
