package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.entity.PriceRule;
import com.jxbg.ticket.service.PriceRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price-rules")
public class PriceRuleController {

    private final PriceRuleService priceRuleService;

    @GetMapping
    public ApiResult<List<PriceRule>> list(@RequestParam(required = false) Long activityId) {
        LambdaQueryWrapper<PriceRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(activityId != null, PriceRule::getActivityId, activityId);
        return ApiResult.success(priceRuleService.list(wrapper));
    }

    @PostMapping
    public ApiResult<Long> create(@RequestBody PriceRule priceRule) {
        priceRuleService.save(priceRule);
        return ApiResult.success(priceRule.getId());
    }
}
