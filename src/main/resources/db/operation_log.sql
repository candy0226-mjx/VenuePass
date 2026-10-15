USE ticket_boot;

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID，系统任务为空',
    operator_role VARCHAR(32) DEFAULT NULL COMMENT '操作人角色：ADMIN/USER/CHECKER/SYSTEM',
    operation_type VARCHAR(64) NOT NULL COMMENT '操作类型',
    business_type VARCHAR(64) NOT NULL COMMENT '业务类型',
    business_id BIGINT DEFAULT NULL COMMENT '业务ID',
    content VARCHAR(500) NOT NULL COMMENT '操作内容',
    result VARCHAR(32) NOT NULL COMMENT '结果：SUCCESS/FAILED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_log_business (business_type, business_id),
    KEY idx_log_operator (operator_id),
    KEY idx_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
