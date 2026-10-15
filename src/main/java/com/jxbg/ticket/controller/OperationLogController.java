package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.entity.OperationLog;
import com.jxbg.ticket.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    public ApiResult<PageResult<OperationLog>> list(@RequestParam(required = false) String businessType,
                                                    @RequestParam(required = false) Long businessId,
                                                    @RequestParam(required = false) Long operatorId,
                                                    @RequestParam(defaultValue = "1") Long pageNo,
                                                    @RequestParam(defaultValue = "10") Long pageSize) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(businessType != null && !businessType.isBlank(), OperationLog::getBusinessType, businessType)
                .eq(businessId != null, OperationLog::getBusinessId, businessId)
                .eq(operatorId != null, OperationLog::getOperatorId, operatorId)
                .orderByDesc(OperationLog::getCreateTime);
        Page<OperationLog> page = operationLogService.page(PageResult.<OperationLog>page(pageNo, pageSize), wrapper);
        return ApiResult.success(PageResult.of(page));
    }
}
