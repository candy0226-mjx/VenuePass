package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.entity.Seat;
import com.jxbg.ticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/areas/{areaId}/seats")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ApiResult<List<Seat>> list(@PathVariable Long areaId) {
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seat::getAreaId, areaId);
        return ApiResult.success(seatService.list(wrapper));
    }

    @PostMapping
    public ApiResult<Long> create(@PathVariable Long areaId, @RequestBody Seat seat) {
        seat.setAreaId(areaId);
        seatService.save(seat);
        return ApiResult.success(seat.getId());
    }
}
