package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxbg.ticket.annotation.OperationLogRecord;
import com.jxbg.ticket.annotation.RateLimit;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.dto.CreateOrderRequest;
import com.jxbg.ticket.dto.PayOrderRequest;
import com.jxbg.ticket.entity.ReservationOrder;
import com.jxbg.ticket.service.ReservationOrderService;
import com.jxbg.ticket.vo.CheckinCodeVO;
import com.jxbg.ticket.vo.OrderDetailVO;
import com.jxbg.ticket.vo.SubmitTokenVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "预约订单", description = "下单令牌、创建订单、订单详情、取消、支付和超时关单接口")
public class ReservationOrderController {

    private final ReservationOrderService reservationOrderService;
    private static final long SUBMIT_TOKEN_EXPIRE_SECONDS = 600;

    @GetMapping
    @Operation(summary = "分页查询订单")
    public ApiResult<PageResult<ReservationOrder>> list(@RequestParam(required = false) @Positive(message = "用户ID必须大于0") Long userId,
                                                        @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Long pageNo,
                                                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") Long pageSize) {
        LambdaQueryWrapper<ReservationOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, ReservationOrder::getUserId, userId)
                .orderByDesc(ReservationOrder::getCreateTime);
        Page<ReservationOrder> page = reservationOrderService.page(PageResult.<ReservationOrder>page(pageNo, pageSize), wrapper);
        return ApiResult.success(PageResult.of(page));
    }

    @PostMapping("/timeout/close")
    @Operation(summary = "手动关闭超时订单", description = "管理员手动触发超时未支付订单关闭。")
    public ApiResult<Integer> closeTimeoutOrders(@RequestParam(defaultValue = "50") @Min(value = 1, message = "批量处理数量必须大于0") @Max(value = 200, message = "批量处理数量不能超过200") Integer batchSize) {
        int safeBatchSize = batchSize == null || batchSize <= 0 ? 50 : Math.min(batchSize, 200);
        return ApiResult.success(reservationOrderService.closeTimeoutOrders(safeBatchSize));
    }

    @GetMapping("/submit-token")
    @Operation(summary = "获取下单令牌", description = "用户下单前获取一次性 submitToken，用于防重复提交。")
    @RateLimit(limit = 20, windowSeconds = 60, message = "获取下单令牌过于频繁，请稍后再试")
    public ApiResult<SubmitTokenVO> generateSubmitToken() {
        String token = reservationOrderService.generateSubmitToken();
        return ApiResult.success(new SubmitTokenVO(token, SUBMIT_TOKEN_EXPIRE_SECONDS));
    }

    @PostMapping
    @Operation(summary = "创建预约订单", description = "用户提交 sessionId、seatIds 和 submitToken 创建订单。")
    @RateLimit(limit = 5, windowSeconds = 10, message = "下单请求过于频繁，请稍后再试")
    @OperationLogRecord(operationType = "CREATE_ORDER", businessType = "ORDER",
            businessId = "#data", content = "用户创建预约订单")
    public ApiResult<Long> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResult.success(reservationOrderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询订单详情")
    public ApiResult<OrderDetailVO> detail(@PathVariable @Positive(message = "订单ID必须大于0") Long id) {
        return ApiResult.success(reservationOrderService.getOrderDetail(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "仅允许取消待支付订单，已支付或已核销订单会被状态机拦截。")
    @OperationLogRecord(operationType = "CANCEL_ORDER", businessType = "ORDER",
            businessId = "#p0", content = "用户取消预约订单")
    public ApiResult<Void> cancel(@PathVariable @Positive(message = "订单ID必须大于0") Long id) {
        reservationOrderService.cancelOrder(id);
        return ApiResult.success();
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "模拟支付", description = "支付待支付订单，成功后生成核销码和支付流水。")
    @RateLimit(limit = 10, windowSeconds = 60, message = "支付请求过于频繁，请稍后再试")
    @OperationLogRecord(operationType = "PAY_ORDER", businessType = "ORDER",
            businessId = "#p0", content = "用户支付预约订单")
    public ApiResult<CheckinCodeVO> pay(@PathVariable @Positive(message = "订单ID必须大于0") Long id,
                                        @Valid @RequestBody(required = false) PayOrderRequest request) {
        return ApiResult.success(reservationOrderService.payOrder(id, request));
    }
}
