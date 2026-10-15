package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.entity.ReserveSession;
import com.jxbg.ticket.service.ReserveSessionService;
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
@RequestMapping("/activities/{activityId}/sessions")
public class ReserveSessionController {

    private final ReserveSessionService reserveSessionService;

    @GetMapping
    public ApiResult<List<ReserveSession>> list(@PathVariable Long activityId) {
        LambdaQueryWrapper<ReserveSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReserveSession::getActivityId, activityId);
        return ApiResult.success(reserveSessionService.list(wrapper));
    }

    @PostMapping
    public ApiResult<Long> create(@PathVariable Long activityId, @RequestBody ReserveSession session) {
        session.setActivityId(activityId);
        reserveSessionService.save(session);
        return ApiResult.success(session.getId());
    }
}
