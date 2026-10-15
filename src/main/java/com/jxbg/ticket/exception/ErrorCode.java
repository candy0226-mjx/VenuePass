package com.jxbg.ticket.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    PARAM_ERROR(400, "请求参数错误"),
    UNAUTHORIZED(401, "请先登录"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    ORDER_STATUS_ERROR(409, "订单状态不允许当前操作"),
    SEAT_ALREADY_RESERVED(409, "座位已被预约"),
    CHECKIN_CODE_INVALID(409, "核销码不可用"),
    SYSTEM_ERROR(500, "系统异常");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
