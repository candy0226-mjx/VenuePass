package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.CheckinRecord;
import com.jxbg.ticket.mapper.CheckinRecordMapper;
import com.jxbg.ticket.service.CheckinRecordService;
import org.springframework.stereotype.Service;

@Service
public class CheckinRecordServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord> implements CheckinRecordService {
}
