package com.jxbg.ticket.service;

import com.jxbg.ticket.enums.OrderStatus;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;

public final class OrderStateMachine {

    private OrderStateMachine() {
    }

    public static void checkCanPay(String currentStatus) {
        if (OrderStatus.PAID.name().equals(currentStatus)) {
            return;
        }
        if (OrderStatus.CANCELLED.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已取消，不能支付");
        }
        if (OrderStatus.CHECKED_IN.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已核销，不能支付");
        }
        checkExpected(currentStatus, OrderStatus.PENDING, "只有待支付订单才能支付");
    }

    public static void checkCanCancel(String currentStatus) {
        if (OrderStatus.PAID.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已支付，不能取消");
        }
        if (OrderStatus.CHECKED_IN.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已核销，不能取消");
        }
        if (OrderStatus.CANCELLED.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已取消，请勿重复取消");
        }
        checkExpected(currentStatus, OrderStatus.PENDING, "只有待支付订单才能取消");
    }

    public static boolean canCloseTimeout(String currentStatus) {
        return OrderStatus.PENDING.name().equals(currentStatus);
    }

    public static void checkCanCheckin(String currentStatus) {
        if (OrderStatus.PENDING.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单未支付，不能核销");
        }
        if (OrderStatus.CANCELLED.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已取消，不能核销");
        }
        if (OrderStatus.CHECKED_IN.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已核销，请勿重复核销");
        }
        checkExpected(currentStatus, OrderStatus.PAID, "只有已支付订单才能核销");
    }

    private static void checkExpected(String currentStatus, OrderStatus expectedStatus, String message) {
        if (!expectedStatus.name().equals(currentStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, message);
        }
    }
}
