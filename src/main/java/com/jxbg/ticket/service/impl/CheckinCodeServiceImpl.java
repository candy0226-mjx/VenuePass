package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.CheckinCode;
import com.jxbg.ticket.mapper.CheckinCodeMapper;
import com.jxbg.ticket.service.CheckinCodeService;
import org.springframework.stereotype.Service;

@Service
public class CheckinCodeServiceImpl extends ServiceImpl<CheckinCodeMapper, CheckinCode> implements CheckinCodeService {
}
