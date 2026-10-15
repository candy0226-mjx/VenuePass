package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.Seat;
import com.jxbg.ticket.mapper.SeatMapper;
import com.jxbg.ticket.service.SeatService;
import org.springframework.stereotype.Service;

@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {
}
