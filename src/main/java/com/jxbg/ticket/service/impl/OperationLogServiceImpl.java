package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.OperationLog;
import com.jxbg.ticket.mapper.OperationLogMapper;
import com.jxbg.ticket.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void record(Long operatorId, String operatorRole, String operationType,
                       String businessType, Long businessId, String content, String result) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setOperatorRole(operatorRole);
        log.setOperationType(operationType);
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setContent(content);
        log.setResult(result);
        save(log);
    }
}
