# 12 当前实现边界与验证清单

本章依据基线 `7661d04` 的静态阅读整理，不是业务实测报告，也不是完整安全审计。风险表示可以从代码推演的失败窗口；修复建议没有在本次学习资料任务中修改业务代码。以后修复时保留问题编号，更新提交、实验和结论。

| 编号 | 源码可见边界 | 影响与研究方向 | 验证 |
|---|---|---|---|
| B01 | 订单详情、支付、取消、核销码查询未统一核对当前用户归属；创建/核销接收外部身份 ID | 角色通过不等于数据权限；从登录上下文取身份并校验对象 | L06 |
| B02 | 场次库存 SQL 采用读取旧值后 updateById 写绝对值，资源锁只针对单 seat | 不同 seat 可同时修改同一场次总量；改为条件原子减/加且核对行数 | L03 |
| B03 | 支付、取消、核销存在先查状态后按 ID 写入；关单单独使用 CAS | 状态覆盖、重复副作用；所有竞争方共同用原子状态迁移 | L03、L06 |
| B04 | 初始化先写数量再写占位 Set，库存存在就跳过初始化；预扣无预占凭证 ID | 半初始化、重建与在途操作交错；设计 ready/版本/预占归属 | L04 |
| B05 | 令牌值为 1，消费后无用户绑定、无结果映射 | 非完整安全重试协议；绑定身份/请求摘要/订单结果 | L06 |
| B06 | Redis 补偿仅为进程内事务回调，热度更新可能先于 DB 提交 | 崩溃或回调失败时漂移；持久补偿、提交后副作用与对账 | L02、L04、L08 |
| B07 | MQ 默认关闭，未显式实现完整 confirm/returns、重试隔离与 Outbox | 编译不是可靠投递验证；先完成真实环境联调 | L07 |
| B08 | 缓存读写协调不完整；新增不主动清除可能的空值；读侧递归重试无明确次数上限 | 旧值回填、短暂不可见、过多等待；失效重试与有界重建 | L05 |
| B09 | 限流 INCR/EXPIRE 两步；按实际 URI；信任转发头；故障放行 | 无 TTL key、计数碎片、伪造 IP；Lua、模板路径和可信代理 | L06、L11 |
| B10 | WebSocket 仅 CONNECT 绑定身份；进程内 simple broker；事务内立即推送 | 订阅授权、多实例路由和未提交通知需补验证 | L09 |
| B11 | 核销失败记录在抛异常的同一事务中；部分日志用 REQUIRES_NEW | 失败尝试可能回滚；日志 SUCCESS 也不一定等于外层最终提交 | L02、L08 |
| B12 | 点赞取消/审批未全面检查竞争更新影响行数 | 计数漂移、矛盾通知；以成功迁移决定唯一副作用 | L08 |
| B13 | 场次主要按馆选资源；唯一性按 session 而非时间区间；价格累加 basePrice | 不具备完整场次资源绑定、重叠时间排他与动态定价 | L01 |
| B14 | 本地文件只校验扩展名等基础条件；榜单/浏览记录无完整保留周期 | 真图片校验、容量、清理和多实例共享仍需设计 | L09、L11 |

## 对应源码入口

- B01、B02、B03、B05、B06：[订单 Controller](../../src/main/java/com/jxbg/ticket/controller/ReservationOrderController.java)、[订单实现](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)、[核销 Controller](../../src/main/java/com/jxbg/ticket/controller/CheckinController.java)。
- B04：[库存实现](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)。
- B07：[MQ 生产者](../../src/main/java/com/jxbg/ticket/mq/RabbitOrderTimeoutMessageProducer.java)、[MQ 消费者](../../src/main/java/com/jxbg/ticket/mq/OrderTimeoutMessageConsumer.java)。
- B08：[场馆缓存](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)。
- B09：[限流切面](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)。
- B10：[WS 配置](../../src/main/java/com/jxbg/ticket/config/WebSocketConfig.java)、[通知服务](../../src/main/java/com/jxbg/ticket/service/impl/UserNotificationServiceImpl.java)。
- B11：[日志切面](../../src/main/java/com/jxbg/ticket/aspect/OperationLogAspect.java)、[独立日志事务](../../src/main/java/com/jxbg/ticket/service/impl/OperationLogServiceImpl.java)。
- B12：[点评实现](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java)、[计数 SQL](../../src/main/java/com/jxbg/ticket/mapper/ReviewMapper.java)。
- B13：[场次资源查询](../../src/main/java/com/jxbg/ticket/controller/SessionSeatController.java)、[表结构](../../src/main/resources/db/schema.sql)。
- B14：[文件实现](../../src/main/java/com/jxbg/ticket/service/impl/FileUploadServiceImpl.java)、[热度服务](../../src/main/java/com/jxbg/ticket/service/impl/HotStatsServiceImpl.java)。

## 不能写成已使用的技术

当前没有集成：Spring Cloud 微服务、Nacos、Seata、XXL-JOB、Elasticsearch、Caffeine、对象存储、Transactional Outbox、真实第三方支付、虚拟线程、生产级 Redis Cluster 与完整链路追踪。手册提到它们是为了比较设计，不是让简历自动增加名词。

## 完成修复后怎样更新

使用记录格式：`B编号 / 修复提交 / 改了哪个原子边界 / 执行实验ID / 实际结果 / 尚存限制`。把问题从“待验证”改成“有证据确认”，再把实现改成“已修复并回归”，不要一改代码就写“彻底解决”。

返回 [目录](README.md)。
