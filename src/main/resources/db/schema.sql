CREATE DATABASE IF NOT EXISTS ticket_boot
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE ticket_boot;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(64) NOT NULL COMMENT '登录名',
    password VARCHAR(100) NOT NULL COMMENT '密码密文',
    nickname VARCHAR(64) NOT NULL COMMENT '昵称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/USER/CHECKER',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_users_username (username),
    KEY idx_users_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS venue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(100) NOT NULL COMMENT '场馆名称',
    address VARCHAR(255) NOT NULL COMMENT '地址',
    description VARCHAR(500) DEFAULT NULL COMMENT '场馆说明',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_venue_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场馆表';

CREATE TABLE IF NOT EXISTS venue_area (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    name VARCHAR(100) NOT NULL COMMENT '区域名称',
    type VARCHAR(32) NOT NULL DEFAULT 'SEAT' COMMENT '区域类型：SEAT/COURT/ROOM',
    capacity INT NOT NULL DEFAULT 0 COMMENT '容量',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_area_venue (venue_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场馆区域表';

CREATE TABLE IF NOT EXISTS seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    area_id BIGINT NOT NULL COMMENT '区域ID',
    seat_no VARCHAR(64) NOT NULL COMMENT '座位/资源编号',
    row_no VARCHAR(32) DEFAULT NULL COMMENT '行号',
    col_no VARCHAR(32) DEFAULT NULL COMMENT '列号',
    name VARCHAR(100) NOT NULL COMMENT '资源名称',
    base_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '基础价格',
    resource_type VARCHAR(32) NOT NULL DEFAULT 'SEAT' COMMENT '资源类型：SEAT/COURT/ROOM',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1可用 0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_seat_area_no (area_id, seat_no),
    KEY idx_seat_venue_area (venue_id, area_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位/资源表';

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    name VARCHAR(100) NOT NULL COMMENT '活动/预约项目名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '说明',
    poster_url VARCHAR(255) DEFAULT NULL COMMENT '海报地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_activity_venue (venue_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动/预约项目表';

CREATE TABLE IF NOT EXISTS `session` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    name VARCHAR(100) NOT NULL COMMENT '场次名称',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    total_stock INT NOT NULL DEFAULT 0 COMMENT '总库存',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '可用库存',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1可预约 0关闭',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_session_activity_time (activity_id, start_time),
    KEY idx_session_venue_time (venue_id, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场次/预约时段表';

CREATE TABLE IF NOT EXISTS price_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    session_id BIGINT DEFAULT NULL COMMENT '场次ID',
    area_id BIGINT DEFAULT NULL COMMENT '区域ID',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_price_activity_session (activity_id, session_id),
    KEY idx_price_area (area_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格规则表';

CREATE TABLE IF NOT EXISTS reservation_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    session_id BIGINT NOT NULL COMMENT '场次ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PAID/CANCELLED/CHECKED_IN',
    expire_time DATETIME NOT NULL COMMENT '支付过期时间',
    paid_at DATETIME DEFAULT NULL COMMENT '支付时间',
    cancelled_at DATETIME DEFAULT NULL COMMENT '取消时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_user (user_id),
    KEY idx_order_session_status (session_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约订单表';

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    session_id BIGINT NOT NULL COMMENT '场次ID',
    seat_id BIGINT NOT NULL COMMENT '座位/资源ID',
    seat_no VARCHAR(64) NOT NULL COMMENT '座位/资源编号快照',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格快照',
    active_flag TINYINT DEFAULT 1 COMMENT '有效占用标记：1占用 NULL已释放',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_item_session_seat_active (session_id, seat_id, active_flag),
    KEY idx_item_order (order_id),
    KEY idx_item_seat (seat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单座位明细表';

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

CREATE TABLE IF NOT EXISTS user_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content VARCHAR(500) NOT NULL COMMENT '通知内容',
    type VARCHAR(32) NOT NULL COMMENT '通知类型',
    business_type VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
    business_id BIGINT DEFAULT NULL COMMENT '业务ID',
    read_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    read_at DATETIME DEFAULT NULL COMMENT '阅读时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_notice_user_read_created (user_id, read_flag, created_at),
    KEY idx_notice_business (business_type, business_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户站内通知表';

CREATE TABLE IF NOT EXISTS checkin_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    code VARCHAR(128) NOT NULL COMMENT '核销码',
    status VARCHAR(32) NOT NULL DEFAULT 'VALID' COMMENT '状态：VALID/USED/EXPIRED',
    expired_at DATETIME NOT NULL COMMENT '过期时间',
    used_at DATETIME DEFAULT NULL COMMENT '使用时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_checkin_code (code),
    UNIQUE KEY uk_checkin_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核销码表';

CREATE TABLE IF NOT EXISTS checkin_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    code_id BIGINT NOT NULL COMMENT '核销码ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    checker_id BIGINT NOT NULL COMMENT '核销员ID',
    result VARCHAR(32) NOT NULL COMMENT '结果：SUCCESS/FAILED',
    message VARCHAR(255) DEFAULT NULL COMMENT '结果说明',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '核销时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_record_order (order_id),
    KEY idx_record_code (code_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核销记录表';

CREATE TABLE IF NOT EXISTS review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    session_id BIGINT NOT NULL COMMENT '场次ID',
    rating TINYINT NOT NULL COMMENT '评分：0-5',
    content VARCHAR(1000) NOT NULL COMMENT '点评内容',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    image_count INT NOT NULL DEFAULT 0 COMMENT '图片数量',
    status TINYINT NOT NULL DEFAULT 2 COMMENT '状态：0隐藏 1已通过 2待审核 3已驳回',
    active_flag TINYINT DEFAULT 1 COMMENT '有效点评标记：1有效 NULL已删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_review_order_active (order_id, active_flag),
    KEY idx_review_venue_created (venue_id, created_at),
    KEY idx_review_activity_created (activity_id, created_at),
    KEY idx_review_user_created (user_id, created_at),
    KEY idx_review_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点评表';

CREATE TABLE IF NOT EXISTS review_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    review_id BIGINT NOT NULL COMMENT '点评ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片地址',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_review_image_review (review_id, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点评图片表';

CREATE TABLE IF NOT EXISTS review_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    review_id BIGINT NOT NULL COMMENT '点评ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    active_flag TINYINT DEFAULT 1 COMMENT '有效点赞标记：1有效 NULL已取消',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_review_like_active (review_id, user_id, active_flag),
    KEY idx_review_like_user (user_id),
    KEY idx_review_like_review (review_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点评点赞表';

INSERT IGNORE INTO users (id, username, password, nickname, phone, role, status)
VALUES
    (1, 'admin', '$2b$12$UyQUImAqRyIO.NPV/mEiGuAf8LvDBf.rn9gAoGte3ZjKvhjCbXRkK', '管理员', '13800000001', 'ADMIN', 1),
    (2, 'user', '$2b$12$UyQUImAqRyIO.NPV/mEiGuAf8LvDBf.rn9gAoGte3ZjKvhjCbXRkK', '普通用户', '13800000002', 'USER', 1),
    (3, 'checker', '$2b$12$UyQUImAqRyIO.NPV/mEiGuAf8LvDBf.rn9gAoGte3ZjKvhjCbXRkK', '核销员', '13800000003', 'CHECKER', 1);

INSERT IGNORE INTO venue (id, name, address, description, contact_phone, status)
VALUES
    (1, '城市体育中心', '示例市人民路 100 号', '支持篮球场、羽毛球场和讲座活动预约', '010-88888888', 1);

INSERT IGNORE INTO venue_area (id, venue_id, name, type, capacity, sort, status)
VALUES
    (1, 1, '一层篮球区', 'COURT', 4, 1, 1),
    (2, 1, '二层讲座区', 'SEAT', 20, 2, 1);

INSERT IGNORE INTO seat (id, venue_id, area_id, seat_no, row_no, col_no, name, base_price, resource_type, status)
VALUES
    (1, 1, 1, 'COURT-01', NULL, NULL, '1 号篮球场', 80.00, 'COURT', 1),
    (2, 1, 1, 'COURT-02', NULL, NULL, '2 号篮球场', 80.00, 'COURT', 1),
    (3, 1, 2, 'A-01', 'A', '01', 'A 排 1 座', 30.00, 'SEAT', 1),
    (4, 1, 2, 'A-02', 'A', '02', 'A 排 2 座', 30.00, 'SEAT', 1);

INSERT IGNORE INTO activity (id, venue_id, name, description, poster_url, status)
VALUES
    (1, 1, '篮球场预约', '城市体育中心篮球场按时段预约', NULL, 1),
    (2, 1, '技术分享会', '二层讲座区座位预约', NULL, 1);

INSERT IGNORE INTO `session` (id, activity_id, venue_id, name, start_time, end_time, total_stock, available_stock, status)
VALUES
    (1, 1, 1, '晚间篮球场 19:00-20:00', '2030-01-01 19:00:00', '2030-01-01 20:00:00', 2, 2, 1),
    (2, 2, 1, '技术分享会 14:00-16:00', '2030-01-02 14:00:00', '2030-01-02 16:00:00', 2, 2, 1);
