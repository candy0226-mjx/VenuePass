package com.jxbg.ticket.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateReviewRequest {

    @NotNull(message = "订单ID不能为空")
    @Positive(message = "订单ID必须大于0")
    private Long orderId;

    @NotNull(message = "评分不能为空")
    @Min(value = 0, message = "评分不能小于0")
    @Max(value = 5, message = "评分不能超过5")
    private Integer rating;

    @NotBlank(message = "点评内容不能为空")
    @Size(max = 1000, message = "点评内容不能超过1000个字符")
    private String content;

    @Size(max = 9, message = "点评图片最多上传9张")
    private List<@NotBlank(message = "图片地址不能为空") @Size(max = 500, message = "图片地址不能超过500个字符") String> imageUrls;
}
