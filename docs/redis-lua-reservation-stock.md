# Redis + Lua 预约库存预扣

## 解决的问题

高并发下大量用户同时预约同一个场次、同一个资源时，如果每次都直接查询数据库并插入订单明细，数据库压力会比较大。

当前实现增加 Redis + Lua 预扣库存：

- Redis 先保存每个场次的可预约库存。
- Redis Set 保存该场次已被占用的资源 ID。
- Lua 脚本原子判断库存、判断资源是否已占用、扣减库存、写入占用资源。
- 数据库事务仍然负责最终落单。
- 数据库唯一索引和 Redisson 座位锁继续作为兜底保护。

## Redis Key

```text
reserve:session:stock:{sessionId}
reserve:session:reserved-seats:{sessionId}
lock:reserve:session:init:{sessionId}
```

## 下单流程

1. 校验用户、场次、座位合法性。
2. 初始化 Redis 场次库存缓存。
3. 执行 Lua 脚本：
   - 座位已在 Set 中，返回座位已预约。
   - 库存不足，返回当前场次可预约资源不足。
   - 校验通过，扣减 Redis 库存并写入占用座位 Set。
4. 注册事务回滚释放逻辑。
5. 继续执行 Redisson 单座位锁、数据库唯一索引、订单入库。
6. 事务提交后 Redis 预扣保留，表示资源已被订单占用。

## 释放流程

取消订单或超时关闭订单时：

1. 数据库订单改为 `CANCELLED`。
2. 订单明细 `active_flag` 置空。
3. 数据库场次库存恢复。
4. 事务提交成功后，释放 Redis 中对应座位占用，并恢复 Redis 库存。

## Lua 返回码

```text
 1  预扣成功
-1  座位已被占用
-2  库存不足
-3  Redis 库存缓存未初始化
```

这个实现适合写到简历中，重点体现 Redis 原子脚本、分布式锁、数据库唯一索引多层防护。
