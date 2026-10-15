package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.ReserveSession;
import com.jxbg.ticket.mapper.ReserveSessionMapper;
import com.jxbg.ticket.service.ReserveSessionService;
import org.springframework.stereotype.Service;

@Service
public class ReserveSessionServiceImpl extends ServiceImpl<ReserveSessionMapper, ReserveSession> implements ReserveSessionService {
}
