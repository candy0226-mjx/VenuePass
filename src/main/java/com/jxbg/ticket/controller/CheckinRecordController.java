package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.entity.CheckinRecord;
import com.jxbg.ticket.service.CheckinRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/checkin/records")
public class CheckinRecordController {

    private final CheckinRecordService checkinRecordService;

    @GetMapping
    public ApiResult<PageResult<CheckinRecord>> list(@RequestParam(required = false) Long orderId,
                                                     @RequestParam(defaultValue = "1") Long pageNo,
                                                     @RequestParam(defaultValue = "10") Long pageSize) {
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(orderId != null, CheckinRecord::getOrderId, orderId)
                .orderByDesc(CheckinRecord::getCreateTime);
        Page<CheckinRecord> page = checkinRecordService.page(PageResult.<CheckinRecord>page(pageNo, pageSize), wrapper);
        return ApiResult.success(PageResult.of(page));
    }
}
