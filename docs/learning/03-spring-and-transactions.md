# 03 Spring、代理与事务

## @Transactional 的工作不是“遇到错误全恢复”

调用进入 Spring 代理后，事务拦截器取得/加入数据库事务，执行目标方法，再根据返回或异常提交/回滚。同一事务中的订单、明细、库存 SQL 才能作为一个数据库单位提交。默认传播 `REQUIRED`；项目常写 `rollbackFor = Exception.class`，包括需要回滚的受检异常。

Redis 的 `DEL`、Lua、ZSet 计分，MQ 发送和 WebSocket 推送不属于这里的 JDBC 本地事务。数据库回滚不会反向执行那些操作。不要把“一个 Java 方法”误当“一个跨中间件原子操作”。[S01、S02](sources.md)

## 自调用到底哪里失效

`this.otherMethod()` 绕开代理，因此 otherMethod 上新增的事务传播或其他切面不会被重新解释。但若调用方已经处于一个数据库事务中，被调用的普通代码仍然在该线程已有事务中执行。

本项目 `closeTimeoutOrders` 自己标了事务，内部调用私有 `closeTimeoutOrder(ReservationOrder)`，不是因为私有方法就“整个操作无事务”。实际是整批扫描和多个订单在外层事务中运行，可能导致事务偏长、锁持有时间增加，并非每个订单独立提交。

JDK 动态代理面向接口；CGLIB 生成子类。实际使用哪一种还取决于 Boot 配置。`private`、`final` 与代理方式有关，不要背成“所有非 public 方法在任意 Spring 版本都不支持事务”。针对本项目优先采用公开 Service 方法作为清晰边界。[S04](sources.md)

## 下单中的三类事务回调

源码：[ReservationOrderServiceImpl](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)。

| 回调注册方法 | 触发阶段 | 意图 | 不保证的事 |
|---|---|---|---|
| `registerReleaseRedisStockAfterRollback` | `afterCompletion` 且回滚 | 数据库没成功时退回预扣 | 进程崩溃后自动重试补偿 |
| `registerUnlockAfterTransaction` | `afterCompletion` | 数据库结束后释放资源锁 | Redis 故障时必然能解锁 |
| `sendCloseOrderMessageAfterCommit` | `afterCommit` | 避免消息先于订单提交被消费 | 提交与发送形成原子事务 |
| `releaseRedisStockAfterCommit` | `afterCommit` | 取消/关单真正提交后释放预扣 | 数据库已提交后 Redis 失败可回滚 DB |

`afterCommit` 调用时数据库已提交。若回调失败，调用方可能看到异常但订单已经生效。需要根据业务 ID 查询确认结果；不能遇到异常就认定“订单没落库”。在回调里再做数据库写入，还要注意没有后续提交的旧事务资源问题，通常用独立事务或异步任务处理。[S03](sources.md)

## 为什么锁在事务完成后释放

假设方法体写完订单后立刻 unlock，而数据库提交还未完成，第二个线程可获取锁，并从自己的快照或当前查询中看到不符合预期的数据。把锁寿命覆盖到事务结束，能使受保护的数据库结果先确定下来。

但“有锁”仍需说明锁名、持有者、租约和所有写入入口。只对同一 seat 加锁，不能保护同场次不同 seat 共享的 `session.available_stock`。数据库总库存回写问题见第 12 章 B02。

## REQUIRES_NEW 操作日志

[OperationLogServiceImpl](../../src/main/java/com/jxbg/ticket/service/impl/OperationLogServiceImpl.java) 的 `record` 使用 `REQUIRES_NEW`，让失败操作的审计日志有机会独立保存。

这也有代价：外层占一个连接，内层再借一个连接，连接池需要留余量；若独立日志记录了 SUCCESS，随后外层事务失败，日志语义可能与最终状态不同。尤其核销 Controller 同时存在事务和日志切面，未显式指定全部切面顺序时，不能仅凭注解书写顺序判断谁包住谁。

`CheckinController.saveFailedRecord` 写的是核销记录，不是上述独立事务日志。它和核销方法在同一事务里写完再抛异常，记录也可能回滚。要区分“审计日志表”和“核销尝试记录表”。[S02](sources.md)

## 从代码走向工程设计

| 失败窗口 | 当前处理 | 可研究的改进 |
|---|---|---|
| Lua 成功，DB 普通异常 | 回滚回调释放预扣 | 为释放失败增加可重试记录 |
| Lua 成功，进程直接退出 | 内存回调不执行 | 预占凭证、到期检查、对账重建 |
| DB 成功，MQ 未发送 | 记录发送异常，定时扫描关单 | Transactional Outbox + 重试 |
| DB 成功，Redis 释放失败 | 不能反向回滚已提交 DB | 释放任务、重试、告警与对账 |

Outbox 指在同一数据库事务内写业务数据和待发送事件，由发送任务投递并标记；不是把 MQ 网络调用塞进 DB 事务就能可靠。这里仅为学习设计，当前仓库没有 Outbox 表。

## 面试口述练习

“本项目数据库事务覆盖订单、明细和库存 SQL。Redis 预扣需要显式补偿，消息在提交后发送，避免消费到未提交订单。内存回调仍有崩溃窗口，因此现在不能宣称跨 Redis、MySQL、RabbitMQ 强一致。”

练习：画出成功、回滚和进程崩溃三张时序图。对应 Q008、Q009、Q010、Q044、Q048；实验 L02、L04。返回 [目录](README.md)。
