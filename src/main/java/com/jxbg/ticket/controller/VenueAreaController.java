package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.entity.VenueArea;
import com.jxbg.ticket.service.VenueAreaService;
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
@RequestMapping("/venues/{venueId}/areas")
public class VenueAreaController {

    private final VenueAreaService venueAreaService;

    @GetMapping
    public ApiResult<List<VenueArea>> list(@PathVariable Long venueId) {
        LambdaQueryWrapper<VenueArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueArea::getVenueId, venueId);
        return ApiResult.success(venueAreaService.list(wrapper));
    }

    @PostMapping
    public ApiResult<Long> create(@PathVariable Long venueId, @RequestBody VenueArea area) {
        area.setVenueId(venueId);
        venueAreaService.save(area);
        return ApiResult.success(area.getId());
    }
}
