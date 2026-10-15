# 设计对齐说明

本文档记录项目第一阶段实现与初始业务设计的对齐情况，作为历史设计记录保留。当前功能和启动方式以根目录 README 为准。

## 已对齐内容

- Java 版本统一为 Java 21。
- 后端技术栈使用 Spring Boot、Spring MVC、MyBatis-Plus、MySQL、Redis、Spring Security、JWT、RabbitMQ、Knife4j、Maven、Docker Compose。
- 数据库核心表已补齐：
  - `users`
  - `venue`
  - `venue_area`
  - `seat`
  - `activity`
  - `session`
  - `price_rule`
  - `reservation_order`
  - `order_item`
  - `payment_record`
  - `checkin_code`
  - `checkin_record`
  - `operation_log`
- 订单状态统一为：
  - `PENDING`
  - `PAID`
  - `CANCELLED`
  - `CHECKED_IN`
- 核销码状态统一为：
  - `VALID`
  - `USED`
  - `EXPIRED`
- `order_item` 已冗余 `session_id`，并增加 `active_flag` 和唯一约束 `uk_item_session_seat_active (session_id, seat_id, active_flag)`。这样能防止同一场次同一资源被重复占用，订单取消后也可以释放资源重新预约。
- 已补齐 `dto`、`vo`、`enums`、`exception`、`security`、`mq`、`job` 包。
- 已增加统一异常处理和业务异常模型。
- 已增加 JWT 基础组件，接口默认需要认证，`/auth/**` 和接口文档路径放行。
- 已配置 `/api` 统一接口前缀。

## 第一阶段已实现主流程

- 创建预约订单的事务流程。
- 创建订单前可获取 Redis 下单令牌，提交时原子删除令牌，防止重复提交。
- 创建订单时校验用户、场次、资源状态和资源重复占用。
- 模拟支付后生成核销码。
- 模拟支付后写入支付流水，支付流水与业务订单分表记录。
- 取消订单时释放资源占用和场次库存。
- 定时任务自动关闭超时未支付订单，并释放资源占用和恢复库存。
- 已补充手动触发超时关单接口，便于开发阶段和 Apifox 演示验证。
- 已补充 RabbitMQ TTL + 死信队列延迟关单能力，创建订单后发送延迟消息，到期后按订单 ID 检查并关闭未支付订单。
- 核销时校验核销码状态、过期时间和订单支付状态。
- 支付接口已支持幂等，重复支付已支付订单时返回已有核销码。
- 核销接口已支持幂等，重复核销已使用核销码时不重复写成功记录。
- 已增加操作日志，记录创建订单、支付、取消、超时关单、核销等关键动作。
- 创建订单、支付订单、取消订单、核销订单已改为 Spring AOP + 自定义注解统一记录操作日志。
- 订单列表、支付流水、核销记录、操作日志已支持 MyBatis-Plus 分页查询。
- 已增加 Redis + Spring AOP 接口限流，保护登录、下单令牌、创建订单、支付、核销等关键接口。
- 已增加场馆详情 Redis 缓存，并通过短 TTL 空值缓存处理缓存穿透。
- 已补充演示数据，便于 Apifox 调试。

## 第一阶段仍可优化

- 创建订单已接入 Redisson 分布式锁，按 `sessionId + seatId` 粒度锁定资源。
- 多资源预约会先对 `seatId` 排序再依次加锁，降低死锁风险。
- Redisson RLock 支持可重入、安全释放和看门狗自动续期，事务完成后统一释放资源锁。
- 数据库唯一约束继续作为最后兜底，防止极端情况下同一场次同一资源被重复占用。
- 支付接口目前是模拟支付，已拆分 `payment_record` 支付流水，后续可继续扩展真实支付回调。
- 核销接口已做状态校验和重复提交幂等控制，角色权限细化放到后续阶段。

## 后续增强

- RabbitMQ 延迟关单已完成，后续可在生产环境默认启用，并保留定时扫描作为补偿任务。
- 消息日志表 `message_log`。
- 简历描述、流程图、面试问答整理。
