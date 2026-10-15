package com.jxbg.ticket.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardSummaryVO {

    private Long todayOrderCount;

    private Long todayPaidOrderCount;

    private BigDecimal todayPaidAmount;

    private Long todayCheckinCount;

    private Long pendingOrderCount;

    private Long totalOrderCount;

    private Long totalVenueCount;

    private Long totalResourceCount;
}
