package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.entity.Activity;
import com.jxbg.ticket.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ApiResult<List<Activity>> list(@RequestParam(required = false) Long venueId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(venueId != null, Activity::getVenueId, venueId);
        return ApiResult.success(activityService.list(wrapper));
    }

    @PostMapping
    public ApiResult<Long> create(@RequestBody Activity activity) {
        activityService.save(activity);
        return ApiResult.success(activity.getId());
    }

    @GetMapping("/{id}")
    public ApiResult<Activity> detail(@PathVariable Long id) {
        return ApiResult.success(activityService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        activityService.updateById(activity);
        return ApiResult.success();
    }
}
