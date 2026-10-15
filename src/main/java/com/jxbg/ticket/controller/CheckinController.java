package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.annotation.OperationLogRecord;
import com.jxbg.ticket.annotation.RateLimit;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.OperationLogContext;
import com.jxbg.ticket.dto.CheckinVerifyRequest;
import com.jxbg.ticket.entity.CheckinCode;
import com.jxbg.ticket.entity.CheckinRecord;
import com.jxbg.ticket.entity.OrderItem;
import com.jxbg.ticket.entity.ReservationOrder;
import com.jxbg.ticket.enums.CheckinCodeStatus;
import com.jxbg.ticket.enums.CheckinResult;
import com.jxbg.ticket.enums.OrderStatus;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.service.CheckinCodeService;
import com.jxbg.ticket.service.CheckinRecordService;
import com.jxbg.ticket.service.HotStatsService;
import com.jxbg.ticket.service.OrderItemService;
import com.jxbg.ticket.service.OrderStateMachine;
import com.jxbg.ticket.service.ReservationOrderService;
import com.jxbg.ticket.service.UserNotificationService;
import com.jxbg.ticket.vo.CheckinCodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "核销管理", description = "订单核销码查询和核销员核销接口")
public class CheckinController {

    private final ReservationOrderService reservationOrderService;
    private final CheckinCodeService checkinCodeService;
    private final CheckinRecordService checkinRecordService;
    private final UserNotificationService userNotificationService;
    private final OrderItemService orderItemService;
    private final HotStatsService hotStatsService;

    @GetMapping("/orders/{orderId}/checkin-code")
    @Operation(summary = "查询订单核销码", description = "用户支付成功后查询订单对应的核销码。")
    public ApiResult<CheckinCodeVO> getCheckinCode(@PathVariable @Positive(message = "订单ID必须大于0") Long orderId) {
        CheckinCode code = checkinCodeService.getOne(new LambdaQueryWrapper<CheckinCode>()
                .eq(CheckinCode::getOrderId, orderId));
        if (code == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "核销码不存在");
        }
        CheckinCodeVO vo = new CheckinCodeVO();
        BeanUtils.copyProperties(code, vo);
        return ApiResult.success(vo);
    }

    @PostMapping("/checkin/verify")
    @Operation(summary = "核销预约码", description = "核销员提交核销码，成功后订单状态变为 CHECKED_IN。")
    @Transactional(rollbackFor = Exception.class)
    @RateLimit(limit = 20, windowSeconds = 60, message = "核销请求过于频繁，请稍后再试")
    @OperationLogRecord(operationType = "CHECKIN_ORDER", businessType = "ORDER", content = "核销员核销预约码")
    public ApiResult<Void> verify(@Valid @RequestBody CheckinVerifyRequest request) {
        CheckinCode code = checkinCodeService.getOne(new LambdaQueryWrapper<CheckinCode>()
                .eq(CheckinCode::getCode, request.getCode()));
        if (code == null) {
            throw new BusinessException(ErrorCode.CHECKIN_CODE_INVALID, "核销码不存在");
        }
        OperationLogContext.setBusinessId(code.getOrderId());
        if (CheckinCodeStatus.USED.name().equals(code.getStatus())) {
            return ApiResult.success("该预约已核销", null);
        }
        if (CheckinCodeStatus.EXPIRED.name().equals(code.getStatus())) {
            throw new BusinessException(ErrorCode.CHECKIN_CODE_INVALID, "核销码已过期");
        }
        if (!CheckinCodeStatus.VALID.name().equals(code.getStatus())) {
            saveFailedRecord(code, request.getCheckerId(), "核销码状态不可用");
            throw new BusinessException(ErrorCode.CHECKIN_CODE_INVALID);
        }
        if (code.getExpiredAt().isBefore(LocalDateTime.now())) {
            code.setStatus(CheckinCodeStatus.EXPIRED.name());
            checkinCodeService.updateById(code);
            saveFailedRecord(code, request.getCheckerId(), "核销码已过期");
            throw new BusinessException(ErrorCode.CHECKIN_CODE_INVALID, "核销码已过期");
        }

        ReservationOrder savedOrder = reservationOrderService.getById(code.getOrderId());
        if (savedOrder == null) {
            saveFailedRecord(code, request.getCheckerId(), "订单不存在");
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        try {
            OrderStateMachine.checkCanCheckin(savedOrder.getStatus());
        } catch (BusinessException ex) {
            saveFailedRecord(code, request.getCheckerId(), ex.getMessage());
            throw ex;
        }

        code.setStatus(CheckinCodeStatus.USED.name());
        code.setUsedAt(LocalDateTime.now());
        checkinCodeService.updateById(code);

        ReservationOrder order = new ReservationOrder();
        order.setId(code.getOrderId());
        order.setStatus(OrderStatus.CHECKED_IN.name());
        reservationOrderService.updateById(order);

        CheckinRecord record = new CheckinRecord();
        record.setCodeId(code.getId());
        record.setOrderId(code.getOrderId());
        record.setCheckerId(request.getCheckerId());
        record.setResult(CheckinResult.SUCCESS.name());
        record.setMessage("核销成功");
        checkinRecordService.save(record);
        hotStatsService.recordOrderCheckedIn(savedOrder.getSessionId(), orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, savedOrder.getId()))
                .stream()
                .map(OrderItem::getSeatId)
                .toList());
        userNotificationService.createAndPush(savedOrder.getUserId(),
                "核销成功",
                "订单 " + savedOrder.getOrderNo() + " 已完成核销，可以发布点评。",
                "CHECKIN",
                "ORDER",
                savedOrder.getId());
        return ApiResult.success();
    }

    private void saveFailedRecord(CheckinCode code, Long checkerId, String message) {
        CheckinRecord record = new CheckinRecord();
        record.setCodeId(code.getId());
        record.setOrderId(code.getOrderId());
        record.setCheckerId(checkerId);
        record.setResult(CheckinResult.FAILED.name());
        record.setMessage(message);
        checkinRecordService.save(record);
    }
}
