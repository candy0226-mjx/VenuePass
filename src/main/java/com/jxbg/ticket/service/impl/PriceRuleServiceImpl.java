package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.PriceRule;
import com.jxbg.ticket.mapper.PriceRuleMapper;
import com.jxbg.ticket.service.PriceRuleService;
import org.springframework.stereotype.Service;

@Service
public class PriceRuleServiceImpl extends ServiceImpl<PriceRuleMapper, PriceRule> implements PriceRuleService {
}
