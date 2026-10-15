package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.OrderItem;
import com.jxbg.ticket.mapper.OrderItemMapper;
import com.jxbg.ticket.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
}
