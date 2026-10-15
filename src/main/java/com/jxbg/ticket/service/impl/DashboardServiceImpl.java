package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.entity.CheckinRecord;
import com.jxbg.ticket.entity.ReservationOrder;
import com.jxbg.ticket.entity.Seat;
import com.jxbg.ticket.entity.Venue;
import com.jxbg.ticket.enums.CheckinResult;
import com.jxbg.ticket.enums.OrderStatus;
import com.jxbg.ticket.enums.PaymentStatus;
import com.jxbg.ticket.mapper.PaymentRecordMapper;
import com.jxbg.ticket.service.CheckinRecordService;
import com.jxbg.ticket.service.DashboardService;
import com.jxbg.ticket.service.ReservationOrderService;
import com.jxbg.ticket.service.SeatService;
import com.jxbg.ticket.service.VenueService;
import com.jxbg.ticket.vo.DashboardSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ReservationOrderService reservationOrderService;
    private final CheckinRecordService checkinRecordService;
    private final VenueService venueService;
    private final SeatService seatService;
    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    public DashboardSummaryVO getSummary() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = todayStart.plusDays(1);

        DashboardSummaryVO vo = new DashboardSummaryVO();
        vo.setTodayOrderCount(countTodayOrders(todayStart, tomorrowStart));
        vo.setTodayPaidOrderCount(countTodayPaidOrders(todayStart, tomorrowStart));
        vo.setTodayPaidAmount(sumTodayPaidAmount(todayStart, tomorrowStart));
        vo.setTodayCheckinCount(countTodayCheckins(todayStart, tomorrowStart));
        vo.setPendingOrderCount(countPendingOrders());
        vo.setTotalOrderCount(reservationOrderService.count());
        vo.setTotalVenueCount(countEnabledVenues());
        vo.setTotalResourceCount(countEnabledResources());
        return vo;
    }

    private Long countTodayOrders(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
        return reservationOrderService.count(new LambdaQueryWrapper<ReservationOrder>()
                .ge(ReservationOrder::getCreateTime, todayStart)
                .lt(ReservationOrder::getCreateTime, tomorrowStart));
    }

    private Long countTodayPaidOrders(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
        return reservationOrderService.count(new LambdaQueryWrapper<ReservationOrder>()
                .eq(ReservationOrder::getStatus, OrderStatus.PAID.name())
                .ge(ReservationOrder::getPaidAt, todayStart)
                .lt(ReservationOrder::getPaidAt, tomorrowStart));
    }

    private BigDecimal sumTodayPaidAmount(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
        BigDecimal amount = paymentRecordMapper.sumAmountByStatusAndPaidAtBetween(
                PaymentStatus.SUCCESS.name(), todayStart, tomorrowStart);
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private Long countTodayCheckins(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
        return checkinRecordService.count(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getResult, CheckinResult.SUCCESS.name())
                .ge(CheckinRecord::getCreateTime, todayStart)
                .lt(CheckinRecord::getCreateTime, tomorrowStart));
    }

    private Long countPendingOrders() {
        return reservationOrderService.count(new LambdaQueryWrapper<ReservationOrder>()
                .eq(ReservationOrder::getStatus, OrderStatus.PENDING.name()));
    }

    private Long countEnabledVenues() {
        return venueService.count(new LambdaQueryWrapper<Venue>()
                .eq(Venue::getStatus, 1));
    }

    private Long countEnabledResources() {
        return seatService.count(new LambdaQueryWrapper<Seat>()
                .eq(Seat::getStatus, 1));
    }
}
