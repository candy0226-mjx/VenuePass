package com.jxbg.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxbg.ticket.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    void record(Long operatorId, String operatorRole, String operationType,
                String businessType, Long businessId, String content, String result);
}
