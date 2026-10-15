USE ticket_boot;

CREATE TABLE IF NOT EXISTS payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    payment_no VARCHAR(64) NOT NULL COMMENT '支付流水号',
    amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    status VARCHAR(32) NOT NULL DEFAULT 'SUCCESS' COMMENT '状态：SUCCESS/FAILED',
    paid_at DATETIME NOT NULL COMMENT '支付时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_payment_order (order_id),
    UNIQUE KEY uk_payment_no (payment_no),
    KEY idx_payment_paid_at (paid_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';
