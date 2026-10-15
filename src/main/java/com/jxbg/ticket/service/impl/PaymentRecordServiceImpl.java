package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.PaymentRecord;
import com.jxbg.ticket.mapper.PaymentRecordMapper;
import com.jxbg.ticket.service.PaymentRecordService;
import org.springframework.stereotype.Service;

@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentRecordService {
}
