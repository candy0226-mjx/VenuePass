package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.entity.PaymentRecord;
import com.jxbg.ticket.service.PaymentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentRecordController {

    private final PaymentRecordService paymentRecordService;

    @GetMapping
    public ApiResult<PageResult<PaymentRecord>> list(@RequestParam(required = false) Long orderId,
                                                     @RequestParam(defaultValue = "1") Long pageNo,
                                                     @RequestParam(defaultValue = "10") Long pageSize) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(orderId != null, PaymentRecord::getOrderId, orderId)
                .orderByDesc(PaymentRecord::getPaidAt);
        Page<PaymentRecord> page = paymentRecordService.page(PageResult.<PaymentRecord>page(pageNo, pageSize), wrapper);
        return ApiResult.success(PageResult.of(page));
    }
}
