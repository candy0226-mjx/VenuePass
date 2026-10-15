package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.entity.OrderItem;
import com.jxbg.ticket.entity.ReserveSession;
import com.jxbg.ticket.entity.Seat;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.service.OrderItemService;
import com.jxbg.ticket.service.ReserveSessionService;
import com.jxbg.ticket.service.SeatService;
import com.jxbg.ticket.vo.SeatAvailabilityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionSeatController {

    private final ReserveSessionService reserveSessionService;
    private final SeatService seatService;
    private final OrderItemService orderItemService;

    @GetMapping("/{sessionId}/seats")
    public ApiResult<List<SeatAvailabilityVO>> listSessionSeats(@PathVariable Long sessionId) {
        ReserveSession session = reserveSessionService.getById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "场次不存在");
        }
        boolean hasSessionStock = session.getAvailableStock() != null && session.getAvailableStock() > 0;

        Set<Long> reservedSeatIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getSessionId, sessionId)
                        .eq(OrderItem::getActiveFlag, 1))
                .stream()
                .map(OrderItem::getSeatId)
                .collect(Collectors.toSet());

        List<SeatAvailabilityVO> seats = seatService.list(new LambdaQueryWrapper<Seat>()
                        .eq(Seat::getVenueId, session.getVenueId())
                        .eq(Seat::getStatus, 1))
                .stream()
                .map(seat -> {
                    SeatAvailabilityVO vo = new SeatAvailabilityVO();
                    BeanUtils.copyProperties(seat, vo);
                    vo.setSeatId(seat.getId());
                    vo.setPrice(seat.getBasePrice());
                    vo.setAvailable(hasSessionStock && !reservedSeatIds.contains(seat.getId()));
                    return vo;
                })
                .toList();
        return ApiResult.success(seats);
    }
}
