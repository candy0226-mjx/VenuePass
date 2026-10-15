# 04 Redis、Lua 与并发控制

## 从竞争条件出发

两个人都执行“查座位没订 -> 创建订单”，可能都看到空闲。问题发生在读与写之间。解决办法可以在不同层建立不可分割的判断与修改，但每层都有适用范围。

| 层 | 当前机制 | 负责什么 |
|---|---|---|
| Redis | Lua 同时判断资源集合与数量并预扣 | 提前拒绝相同场次的重复占位与数量不足 |
| Redis 客户端协调 | Redisson 资源锁 | 同 `sessionId + seatId` 的临界区互斥 |
| MySQL | 唯一索引 | 阻止重复有效占位最终落库 |
| MySQL | 本地事务 | 同一数据库业务写入共同提交/回滚 |

这些机制不自动互相替代。也不要为了显得复杂而无条件堆锁：需要说明在给定架构中哪层是准入、哪层是事实约束、哪层是冗余防御。

## Redis 数据结构选择

| 数据 | Key 示例 | 结构与原因 |
|---|---|---|
| 库存数 | `reserve:session:stock:1` | String，可 `DECRBY/INCRBY` |
| 已预占资源 | `reserve:session:reserved-seats:1` | Set，用 `SISMEMBER/SADD/SREM` |
| 下单令牌 | `order:submit:token:xxx` | String + TTL，一次性消费 |
| 场馆缓存 | `cache:venue:detail:1` | JSON String，含空值哨兵 |
| 热度 | `rank:venue:hot` | ZSet，成员唯一、分数排序 |
| 点赞用户 | `review:like:1:users` | Set，判断用户是否已点赞 |

结构是逻辑接口，底层编码可能随数据量和版本改变。ZSet 可用紧凑编码；较大数据通常需要哈希查找配合跳表排序，不能说“任何 ZSet 永远只有跳表”。[S17](sources.md)

## 精读预约脚本

[ReservationStockServiceImpl](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java) 的 `RESERVE_SCRIPT`：

1. 库存 key 不存在，返回 `-3`。
2. 任一资源在占位 Set 中，返回 `-1`。
3. 数量小于本次资源数，返回 `-2`。
4. `DECRBY` 扣数量，把资源加入 Set，返回 `1`。

脚本保证执行期间没有其他客户端命令插入这段操作，并减少网络往返。它不会把 MySQL 也纳入原子范围；脚本运行时错误也不是数据库式自动回滚。脚本应先完成校验，再做写入，约束 key 类型、执行量和运行时间。[S05、S06](sources.md)

释放脚本按 `SREM` 实际移除数量 `INCRBY`。重复释放同一集合通常不会再次增加数量，但它只按 seatId 标识占用，没有 reservationId/版本。若一个延迟的旧补偿在新预订之后执行，可能碰到同 seat 的新占用；更严谨的设计需要可核对的预占所有者和补偿事件 ID。

## 初始化为什么也需要一致性

`ensureStockCache` 先判断库存 key，不存在才拿初始化锁，再查 DB 写回。二次判断减少重复重建。

当前代码先 SET 库存 key，再查有效明细并写 Set。在这两个步骤之间，其他请求看到库存 key 存在就会绕过初始化锁。若节点在中途失败，数据也可能半初始化。即使最终 DB 唯一索引拦住重复订单，Redis 准入状态仍可能不完整。

可研究“数据构建后最后发布 ready 标记”、临时 key + 原子发布、初始化版本号和一致快照，并约束初始化与实时预约共享的协调边界。这些是改进设计，不是当前已完成的功能。

## Redisson 锁是怎样工作的

当前资源锁名是 `lock:reserve:session:{sessionId}:seat:{seatId}`，源码拼接的实际 key 如 `lock:reserve:session:1:seat:2`。花括号在上述描述里是变量占位，不是 Redis Cluster hash tag。

资源锁调用 `tryLock(0, TimeUnit.SECONDS)`，立即尝试，不等待；未指定固定 leaseTime，使用 Redisson 的续期机制。Redisson 常规 RLock 以客户端与线程身份区分持有者，并支持重入计数；加解锁操作借助 Redis 脚本。[S09](sources.md)

资源锁与初始化锁不同：初始化锁 `tryLock(1, 5, SECONDS)` 指定 5 秒租约，不能把所有锁都说成看门狗自动续期。缓存重建锁同样是固定 5 秒租约。

看门狗延长仍由活跃客户端持有的锁的 TTL，进程退出后靠 TTL 释放。但长时间 GC 暂停、网络隔离、故障切换都可能破坏你以为的独占。对不可重复的最终写入仍用数据库条件/约束；更强场景可研究 fencing token，要求下游拒绝旧持有者版本。

## 为什么资源排序后加锁

请求 A 要 `[1,2]`，B 要 `[2,1]`，不同加锁顺序可能形成环路。排序让资源获取有统一顺序。当前还采用不等待，失败时释放已获得的锁，所以会快速失败；仍需说明失败重试的限流、退避与用户体验。

锁粒度越细，互不相关的资源吞吐越好，但共享场次计数不能因此自动串行。数量读改写丢更新见 B02。

## 从单机 Redis 到 Cluster

预约 Lua 涉及库存与 Set 两个 key。Redis Cluster 要求同一脚本访问的相关 key 位于同一槽。当前 key 没有共享显式 hash tag，不能原样宣称适配 Cluster。

改进命名示例：`reserve:session:{1}:stock`、`reserve:session:{1}:seats`。两者使用相同字面 `{1}` 做 hash tag，注意是脚本访问的 key 要同槽，并非所有 Redis key 都集中到一个槽。[S08](sources.md)

## 必会追问

Lua 与 MULTI/EXEC、Pipeline 有何区别？Pipeline 主要合并网络往返，不提供整个业务的条件原子性。MULTI/EXEC 把命令排队执行，可用 WATCH 乐观检查，但不能在客户端无往返地任意读取中间结果再分支。Lua 适合短小的条件逻辑；长脚本会影响其他请求。

对应 Q019-Q027、Q063；实验 L03、L04。返回 [目录](README.md)。
