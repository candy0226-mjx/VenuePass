USE ticket_boot;

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
