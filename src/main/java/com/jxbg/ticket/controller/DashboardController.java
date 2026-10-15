package com.jxbg.ticket.controller;

import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.service.DashboardService;
import com.jxbg.ticket.vo.DashboardSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
@Tag(name = "工作台统计", description = "后台首页统计接口")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "查询工作台统计", description = "统计场馆、订单、支付、点评等核心数据。")
    public ApiResult<DashboardSummaryVO> summary() {
        return ApiResult.success(dashboardService.getSummary());
    }
}
