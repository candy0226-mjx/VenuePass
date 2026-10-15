# VenuePass 面试题集

本文件由 `python tools/study.py export` 生成。请编辑 questions.json 或使用维护工具。

题目按源码与官方原理重新组织，网上主题来源见 sources.md。优先级为学习顺序，不代表真实面试概率。

共 72 题。状态：todo 未学 / learning 学习中 / mastered 能脱稿讲解。

[学习目录](README.md) · [引用来源](sources.md) · [当前实现边界](12-implementation-boundaries.md)

| 编号 | 主题 | 优先级 | 状态 | 题目 |
|---|---|---|---|---|
| [Q001](#q001) | 项目设计 | P0 | todo | 用一分钟说明 VenuePass 解决什么问题、采用什么架构。 |
| [Q002](#q002) | 项目设计 | P0 | todo | sessionId、seatId、场次库存分别代表什么？ |
| [Q003](#q003) | 项目设计 | P0 | todo | 不同场次预约同一个资源一定不会时间冲突吗？ |
| [Q004](#q004) | 项目设计 | P1 | todo | 为什么保存订单价格快照，价格规则现在真正参与计费了吗？ |
| [Q005](#q005) | Spring与结构 | P1 | todo | Controller、Service、Mapper 和 DTO/Entity/VO 为什么要区分？ |
| [Q006](#q006) | Spring与结构 | P0 | todo | 从收到 POST /orders 到返回订单 ID，按源码顺序讲一次。 |
| [Q007](#q007) | Spring与结构 | P1 | todo | 单例 Service 是否线程安全？构造器注入解决了什么？ |
| [Q008](#q008) | 事务 | P0 | todo | Spring AOP 自调用会使整个业务都没有事务吗？ |
| [Q009](#q009) | 事务 | P0 | todo | createOrder 回滚会恢复 Redis、热度和下单令牌吗？ |
| [Q010](#q010) | 事务 | P0 | todo | afterCommit、afterCompletion 与方法返回是什么关系？ |
| [Q011](#q011) | 数据库并发 | P0 | todo | 状态机为什么不能单独保证支付和关单不会竞争？ |
| [Q012](#q012) | 数据库并发 | P0 | todo | 不同座位都加了锁，场次库存为什么仍可能丢更新？ |
| [Q013](#q013) | 数据库并发 | P0 | todo | 唯一索引为什么使用 active_flag=1/NULL，而不是 1/0？ |
| [Q014](#q014) | 数据库原理 | P1 | todo | MVCC 与行锁在预约中分别解决什么问题？ |
| [Q015](#q015) | 数据库原理 | P1 | todo | 提高隔离级别是否就能解决所有库存和状态竞争？ |
| [Q016](#q016) | 数据库原理 | P1 | todo | 多资源预约为什么排序加锁，数据库死锁后怎么办？ |
| [Q017](#q017) | 数据库原理 | P1 | todo | 如何为超时订单扫描选择索引，而不是凭感觉建索引？ |
| [Q018](#q018) | 数据库原理 | P1 | todo | MyBatis-Plus 分页就能保证大表查询快吗？ |
| [Q019](#q019) | Redis与锁 | P0 | todo | 为什么库存用 String、占位用 Set、热度用 ZSet？ |
| [Q020](#q020) | Redis与锁 | P0 | todo | Lua 原子性指什么？脚本报错会像数据库一样回滚吗？ |
| [Q021](#q021) | Redis与锁 | P0 | todo | 预约脚本为什么先查资源占用再查库存？返回值有哪些？ |
| [Q022](#q022) | Redis与锁 | P0 | todo | 库存缓存初始化为什么可能出现半初始化？ |
| [Q023](#q023) | Redis与锁 | P0 | todo | 释放脚本按 SREM 回补就代表任何补偿都安全了吗？ |
| [Q024](#q024) | Redis与锁 | P0 | todo | Lua、Redisson、数据库唯一索引为何同时存在？ |
| [Q025](#q025) | Redis与锁 | P0 | todo | Redisson 看门狗怎样工作？项目所有锁都会续期吗？ |
| [Q026](#q026) | Redis与锁 | P1 | todo | 把 Redis 换成 Cluster，预约 Lua 能原样运行吗？ |
| [Q027](#q027) | Redis与锁 | P1 | todo | Redis 故障切换或锁租约到期后，旧持有者还继续执行怎么办？ |
| [Q028](#q028) | 缓存 | P0 | todo | 项目使用哪种缓存读写方式，是否已经用逻辑过期？ |
| [Q029](#q029) | 缓存 | P0 | todo | 缓存空值怎样缓解穿透，为什么 TTL 比正常值短？ |
| [Q030](#q030) | 缓存 | P0 | todo | 互斥重建怎样缓解击穿，目前等待策略有什么边界？ |
| [Q031](#q031) | 缓存 | P0 | todo | 先更新数据库再删除缓存，还有哪些不一致窗口？ |
| [Q032](#q032) | 缓存 | P0 | todo | Redis 不可用时是否应该所有请求直接查数据库？ |
| [Q033](#q033) | 安全与幂等 | P0 | todo | JWT 与 submitToken 分别解决什么，是否可以互相替代？ |
| [Q034](#q034) | 安全与幂等 | P0 | todo | 为什么消费下单令牌不用先 GET 再 DELETE？ |
| [Q035](#q035) | 安全与幂等 | P0 | todo | 服务端已下单但响应丢失，用户重试应该怎么办？ |
| [Q036](#q036) | 安全与幂等 | P0 | todo | 当前 submitToken 绑定用户了吗，怎样改进？ |
| [Q037](#q037) | 安全与幂等 | P0 | todo | 项目有角色权限，为何还需要验证订单属于当前用户？ |
| [Q038](#q038) | 安全与幂等 | P1 | todo | 禁用用户或修改角色后，旧 JWT 会立即失效吗？ |
| [Q039](#q039) | 安全与幂等 | P1 | todo | BCrypt、JWT 签名、HTTPS 分别保护什么？ |
| [Q040](#q040) | 安全与幂等 | P0 | todo | 为什么 Apifox 显示 HTTP 200，但业务仍然是失败？ |
| [Q041](#q041) | 消息与调度 | P0 | todo | 延迟消息里面是什么，过期之后谁处理？ |
| [Q042](#q042) | 消息与调度 | P0 | todo | Publisher confirm 与 consumer ACK 有什么区别？当前做到了哪一步？ |
| [Q043](#q043) | 消息与调度 | P0 | todo | 重复收到关单消息，怎样避免重复释放库存？ |
| [Q044](#q044) | 消息与调度 | P0 | todo | 为什么事务提交后发 MQ 还会丢事件，Outbox 改善什么？ |
| [Q045](#q045) | 消息与调度 | P1 | todo | TTL 到期为什么不保证订单刚好 15 分钟关闭？ |
| [Q046](#q046) | 消息与调度 | P1 | todo | 应用部署两个实例，@Scheduled 会只执行一次吗？ |
| [Q047](#q047) | 消息与调度 | P1 | todo | 订单长期不关闭或消费积压，如何定位？ |
| [Q048](#q048) | AOP与限流 | P1 | todo | AOP 操作日志为何使用 REQUIRES_NEW，是否一定反映最终成功？ |
| [Q049](#q049) | AOP与限流 | P1 | todo | OperationLogContext 为什么要在 finally 中清理 ThreadLocal？ |
| [Q050](#q050) | AOP与限流 | P0 | todo | 当前限流是什么算法，INCR 与 EXPIRE 分开有什么风险？ |
| [Q051](#q051) | AOP与限流 | P1 | todo | 限流 Redis 异常时放行、直接信任 X-Forwarded-For，有何取舍？ |
| [Q052](#q052) | 点评与通知 | P0 | todo | 点赞为什么使用 Redis Set 加数据库唯一索引，取消时如何避免计数漂移？ |
| [Q053](#q053) | 点评与通知 | P1 | todo | 点评分页如何避免逐条查作者和图片的 N+1 问题？ |
| [Q054](#q054) | 点评与通知 | P1 | todo | 热门场馆是如何排名的？它是严格的业务统计吗？ |
| [Q055](#q055) | 点评与通知 | P0 | todo | 既然用 WebSocket，为什么还要存 user_notification？ |
| [Q056](#q056) | 点评与通知 | P0 | todo | WebSocket 在 CONNECT 验 JWT 就完成安全控制了吗？ |
| [Q057](#q057) | 工程安全 | P1 | todo | 上传只检查 jpg/png 扩展名够吗，项目做了哪些检查？ |
| [Q058](#q058) | 工程性能 | P1 | todo | POI 导出几千行和几十万行有什么差别？ |
| [Q059](#q059) | 测试与排障 | P0 | todo | 怎样验证项目的并发正确性，而不只是拿到一个 QPS 数字？ |
| [Q060](#q060) | 测试与排障 | P0 | todo | 用户说资源一直不可预约，你按什么顺序排查？ |
| [Q061](#q061) | Java基础 | P1 | todo | 金额为什么用 BigDecimal，equals 与 compareTo 有什么区别？ |
| [Q062](#q062) | Java基础 | P1 | todo | volatile、synchronized 和 Redis 锁能互相替换吗？ |
| [Q063](#q063) | Java基础 | P1 | todo | seatIds 去重排序、批量查询结果建 Map 有什么意义？ |
| [Q064](#q064) | Java基础 | P1 | todo | LocalDateTime、过期时间与随机订单号有什么工程边界？ |
| [Q065](#q065) | 部署与运维 | P0 | todo | 别人克隆仓库如何启动，哪些配置绝不能直接公开部署？ |
| [Q066](#q066) | 部署与运维 | P1 | todo | 一次业务故障需要记录哪些日志与指标？ |
| [Q067](#q067) | 工程性能 | P1 | todo | 接口慢时，把线程池和数据库连接池调大就能解决吗？ |
| [Q068](#q068) | 部署与运维 | P1 | todo | schema.sql 加一列，线上数据库重跑就自动升级了吗？ |
| [Q069](#q069) | 测试与排障 | P0 | todo | 怎样严谨地写缓存优化与压测成果？ |
| [Q070](#q070) | 项目设计 | P1 | todo | 面试官问篮球场活动为何能看到讲座座位，怎么回答？ |
| [Q071](#q071) | 学习与设计 | P1 | todo | 如何保证学习资料后来能增删改查且不与源码脱节？ |
| [Q072](#q072) | 学习与设计 | P0 | todo | 这个项目哪些设计思想可迁移，哪些能力暂时不能宣称？ |

<a id="q001"></a>
## Q001 用一分钟说明 VenuePass 解决什么问题、采用什么架构。

主题：项目设计；优先级：P0；状态：todo。

**参考回答**

它把场馆内的座位、球场或房间统一为资源，用户按场次预约，完成模拟支付、凭码核销与核销后点评。当前是 Spring Boot 单体后端，MySQL 保存业务事实，Redis 承担缓存与准入控制，可选 RabbitMQ 安排超时检查。引入 Redisson 不等于已经微服务化，也不能据此给出未经测量的并发指标。

**继续追问**

- 哪些写入必须保持一致？
- 现在拆微服务会增加什么成本？

**源码入口**

- [pom.xml](../../pom.xml)
- [TicketBootApplication.java](../../src/main/java/com/jxbg/ticket/TicketBootApplication.java)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q002"></a>
## Q002 sessionId、seatId、场次库存分别代表什么？

主题：项目设计；优先级：P0；状态：todo。

**参考回答**

sessionId 标识有开始和结束时间的一场预约，seatId 标识具体资源。库存数量限制这个场次还能接受多少资源，明细占位限制具体哪个资源可被选择。当前唯一性按 sessionId+seatId 判断，不能只凭有数量就允许预订已占用资源。资源查询主要按同馆筛选，尚不等于完成所有活动或区域绑定。

**继续追问**

- 为什么一个场次有多个资源？
- 资源列表有四个、场次库存是二，如何解释？

**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)
- [SessionSeatController.java](../../src/main/java/com/jxbg/ticket/controller/SessionSeatController.java)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q003"></a>
## Q003 不同场次预约同一个资源一定不会时间冲突吗？

主题：项目设计；优先级：P0；状态：todo。

**参考回答**

不一定。当前排他键包含 sessionId，两个不同 session 即使时间重叠也不会因该唯一索引自动冲突。任意时段预约需要检查 existing.start<new.end 且 existing.end>new.start，并把判断与写入置于共同的并发边界。只查询重叠再插入，仍可能发生并发穿过校验。

**继续追问**

- 相邻但不重叠的时间段是否可约？
- 为同一资源协调重叠时段有哪些成本？

**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)：`uk_item_session_seat_active`

原理或主题来源：[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q004"></a>
## Q004 为什么保存订单价格快照，价格规则现在真正参与计费了吗？

主题：项目设计；优先级：P1；状态：todo。

**参考回答**

历史订单应保留下单时的资源编号、单价和总金额，资源后续调价不能重算旧订单。当前 createOrder 主要累加 Seat.basePrice，price_rule 虽有表和接口，但不能据此宣称已实现完整的按场次动态定价。若接入规则，要定义匹配优先级、有效时间和舍入，再将最终计算值固化为快照。

**继续追问**

- 优惠如何分摊到多个资源？
- 退款为什么不能直接取当前价格？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`getBasePrice`
- [schema.sql](../../src/main/resources/db/schema.sql)：`price_rule`

原理或主题来源：[S24](sources.md#s24)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q005"></a>
## Q005 Controller、Service、Mapper 和 DTO/Entity/VO 为什么要区分？

主题：Spring与结构；优先级：P1；状态：todo。

**参考回答**

Controller 处理协议与入参，Service 编排业务和事务，Mapper 访问数据；DTO 限定可输入字段，Entity 映射表，VO 描述响应。这样有助于隔离变化和防止把内部字段直接交给调用者。当前核销业务仍集中在 Controller，部分管理接口接收 Entity，应如实指出这些分层与过度绑定边界。

**继续追问**

- 为何不能让前端任意设置订单状态？
- 把核销下沉到 Service 怎样改善测试？

**源码入口**

- [CreateOrderRequest.java](../../src/main/java/com/jxbg/ticket/dto/CreateOrderRequest.java)
- [OrderDetailVO.java](../../src/main/java/com/jxbg/ticket/vo/OrderDetailVO.java)
- [CheckinController.java](../../src/main/java/com/jxbg/ticket/controller/CheckinController.java)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q006"></a>
## Q006 从收到 POST /orders 到返回订单 ID，按源码顺序讲一次。

主题：Spring与结构；优先级：P0；状态：todo。

**参考回答**

过滤器验 JWT 并设置身份，安全链检查角色，MVC 校验请求并进入限流/日志切面；Service 代理开启事务。方法删除 submitToken，检查用户场次资源，Lua 预扣后注册补偿，再获取资源锁，检查数据库占位，写订单与明细并回写库存。提交后发送延迟消息，完成回调释放锁。注意先 Lua 后锁，且 DB 事务不涵盖 Redis。

**继续追问**

- 提交失败走哪个回调？
- 消息何时发送，为什么？

**源码入口**

- [ReservationOrderController.java](../../src/main/java/com/jxbg/ticket/controller/ReservationOrderController.java)：`create`
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`createOrder`

原理或主题来源：[S01](sources.md#s01)、[S03](sources.md#s03)、[S12](sources.md#s12)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q007"></a>
## Q007 单例 Service 是否线程安全？构造器注入解决了什么？

主题：Spring与结构；优先级：P1；状态：todo。

**参考回答**

单例只表示容器通常共享一个实例，不保证可变成员的并发安全。请求数据应放局部变量，不把当前用户或临时订单放可变字段。构造器注入使依赖显式并便于测试，Lombok RequiredArgsConstructor 为 final 字段生成构造器，但依赖为 final 不会让其内部所有操作自动线程安全。

**继续追问**

- 成员 HashMap 保存用户上下文有什么问题？
- 为什么不用每次 new Service？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`RequiredArgsConstructor`

原理或主题来源：[S30](sources.md#s30)、[C02](sources.md#c02)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q008"></a>
## Q008 Spring AOP 自调用会使整个业务都没有事务吗？

主题：事务；优先级：P0；状态：todo。

**参考回答**

同类内部直接调用绕开代理，所以被调用方法上的新事务语义不会再次生效。但调用方已有事务时，内部普通代码仍在现有线程事务中运行。本项目 closeTimeoutOrders 的外层事务覆盖内部私有关闭方法，实际边界是整批操作而非每个订单独立事务。不能简单看到 private 就说全部事务失效。

**继续追问**

- REQUIRES_NEW 自调用为什么不起作用？
- JDK 代理和 CGLIB 有什么不同？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrders`

原理或主题来源：[S01](sources.md#s01)、[S04](sources.md#s04)、[C02](sources.md#c02)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q009"></a>
## Q009 createOrder 回滚会恢复 Redis、热度和下单令牌吗？

主题：事务；优先级：P0；状态：todo。

**参考回答**

本地数据库事务只能回滚参与的 SQL。项目为预扣库存注册回滚释放回调，但已删除的令牌、事务中增加的热度不会因 DB 回滚自动恢复。普通异常和进程退出也不同：进程退出可能来不及执行内存回调。应分别定义哪些副作用必须补偿、能否重建和失败后的对账机制。

**继续追问**

- 如果回调又遇到 Redis 故障？
- 热度允许不精确是否就可以不做任何容量管理？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`registerReleaseRedisStockAfterRollback`
- [HotStatsServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/HotStatsServiceImpl.java)

原理或主题来源：[S01](sources.md#s01)、[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q010"></a>
## Q010 afterCommit、afterCompletion 与方法返回是什么关系？

主题：事务；优先级：P0；状态：todo。

**参考回答**

方法体返回后，事务代理还需提交或回滚。afterCommit 在提交已成功后执行，afterCompletion 在完成后做清理，包含提交和回滚情况。本项目提交后发 MQ、释放 Redis 预扣，事务完成后解锁。afterCommit 失败不能回滚已提交的订单，因此客户端异常也可能对应已落库结果，需要查询确认并设计补偿。

**继续追问**

- 回调里再写数据库需要注意什么？
- 为什么锁不能只在方法体 finally 中释放？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`registerUnlockAfterTransaction`

原理或主题来源：[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q011"></a>
## Q011 状态机为什么不能单独保证支付和关单不会竞争？

主题：数据库并发；优先级：P0；状态：todo。

**参考回答**

状态机只是对读取到的状态判断合法性，两个线程可能同时读到 PENDING。要通过共同遵守的 UPDATE WHERE status=PENDING 等条件及影响行数取得迁移资格，再触发唯一副作用。当前关单有条件更新，支付/取消/核销未完全统一；只让关单 CAS 而支付仍按 ID 覆盖，仍有取消后变 PAID 的窗口。

**继续追问**

- 影响零行应该怎样返回？
- 支付结果已经由真实渠道成功时还能直接拒绝吗？

**源码入口**

- [OrderStateMachine.java](../../src/main/java/com/jxbg/ticket/service/OrderStateMachine.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`payOrder`

原理或主题来源：[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q012"></a>
## Q012 不同座位都加了锁，场次库存为什么仍可能丢更新？

主题：数据库并发；优先级：P0；状态：todo。

**参考回答**

锁按 seat 区分，两个不同 seat 的请求仍共享 session.available_stock。都读到 2 并各写绝对值 1，数据库最终可能为 1，实际已订两个。需要数据库内 available_stock=available_stock-n 且库存足够的条件更新，并检查行数；释放要原子加且只执行一次。Lua 正确不代表 DB 回写自动正确。

**继续追问**

- 同一事务 RR 隔离能否解决？
- 把锁粒度改成整个 session 有什么代价？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`setAvailableStock`

原理或主题来源：[S10](sources.md#s10)、[S11](sources.md#s11)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q013"></a>
## Q013 唯一索引为什么使用 active_flag=1/NULL，而不是 1/0？

主题：数据库并发；优先级：P0；状态：todo。

**参考回答**

有效占位用 1，联合唯一索引只允许同场次同资源一个有效记录。MySQL UNIQUE 允许多个含 NULL 的记录，释放设 NULL 可保留多次历史。若释放统一写 0，第二次历史释放可能再次冲突于同一组零值。NULL 判断必须使用 IS NULL，也要区分逻辑删除 deleted 与业务释放 active_flag。

**继续追问**

- 删掉 Java 中的先查是否仍能挡重复落库？
- 换数据库时需要重新核对什么语义？

**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)：`uk_item_session_seat_active`

原理或主题来源：[S07](sources.md#s07)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q014"></a>
## Q014 MVCC 与行锁在预约中分别解决什么问题？

主题：数据库原理；优先级：P1；状态：todo。

**参考回答**

MVCC 让普通一致性读借助版本读取合适快照，减少读写阻塞；行锁或条件写入协调竞争修改。读到一个可用快照不代表别人没有随后占位。创建订单还需要唯一约束、原子计数和状态迁移，不能把 MVCC 说成业务排他锁。锁定读与普通 SELECT 的语义也不同。

**继续追问**

- RC 与 RR 快照有什么区别？
- 长事务为什么影响版本清理？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`createOrder`

原理或主题来源：[S10](sources.md#s10)、[S11](sources.md#s11)、[C03](sources.md#c03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q015"></a>
## Q015 提高隔离级别是否就能解决所有库存和状态竞争？

主题：数据库原理；优先级：P1；状态：todo。

**参考回答**

隔离级别决定读写可见性与部分冲突处理，不会修正错误业务边界，也不会把 Redis 纳入事务。当前 Java 读旧库存后 SET 绝对值的逻辑应改为原子 SQL，状态变化应做条件更新。更强隔离通常伴随锁与吞吐成本，必须说明场景和失败重试，不把调高隔离级别当万能修复。

**继续追问**

- Serializable 会带来什么等待？
- 乐观版本与状态条件有什么取舍？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)

原理或主题来源：[S10](sources.md#s10)、[S11](sources.md#s11)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q016"></a>
## Q016 多资源预约为什么排序加锁，数据库死锁后怎么办？

主题：数据库原理；优先级：P1；状态：todo。

**参考回答**

统一获取顺序降低不同请求逆序占有形成环路的概率；当前资源锁不等待，失败时释放已获锁。数据库仍可能因其他表更新和索引访问死锁，需缩短事务、统一顺序、查看死锁证据。重试应针对可重试的完整事务，有限次数加退避，处理令牌已消耗与副作用，不能只重放某条 SQL。

**继续追问**

- Redis 锁顺序相同能保证 DB 无死锁吗？
- 为什么不能在持有 DB 锁时长期等待网络？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`lockSeats`

原理或主题来源：[S10](sources.md#s10)、[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q017"></a>
## Q017 如何为超时订单扫描选择索引，而不是凭感觉建索引？

主题：数据库原理；优先级：P1；状态：todo。

**参考回答**

先列实际条件 status=PENDING、expire_time 范围及排序，再用代表性数据做 EXPLAIN，查看访问索引、估算扫描和排序。可评估 status,expire_time,id 等候选联合索引，但当前索引不等于已最优。还需比较写入成本、空间与不同参数分布；低基数字段在合适联合索引中仍可能有用。

**继续追问**

- EXPLAIN ANALYZE 与 EXPLAIN 有何区别？
- 覆盖索引与回表是什么？

**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)：`idx_order_session_status`
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrders`

原理或主题来源：[S21](sources.md#s21)、[S07](sources.md#s07)、[C03](sources.md#c03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q018"></a>
## Q018 MyBatis-Plus 分页就能保证大表查询快吗？

主题：数据库原理；优先级：P1；状态：todo。

**参考回答**

分页控制返回规模，但深 offset 仍可能扫描大量记录，count 也有成本。可研究 created_at+id 稳定排序和游标分页，结合过滤索引。只按创建时间排序遇到同时间数据可能不稳定。分页插件解决 SQL 改写，不自动解决业务权限、索引选择和一致性。

**继续追问**

- 为什么游标需要额外主键排序？
- 何时可以不返回总数？

**源码入口**

- [MybatisPlusConfig.java](../../src/main/java/com/jxbg/ticket/config/MybatisPlusConfig.java)
- [PageResult.java](../../src/main/java/com/jxbg/ticket/common/PageResult.java)

原理或主题来源：[S25](sources.md#s25)、[S21](sources.md#s21)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q019"></a>
## Q019 为什么库存用 String、占位用 Set、热度用 ZSet？

主题：Redis与锁；优先级：P0；状态：todo。

**参考回答**

库存是可原子增减的数量，String 适合 DECRBY；资源占位要做成员判断和去重，Set 适合 SISMEMBER/SADD/SREM；热度要按分数排序，ZSet 可对成员累加分数并取 TopN。选择以操作需求为依据。ZSet 的实际底层编码会随版本与规模变化，不能一概说每个实例都是跳表。

**继续追问**

- 榜单分数相同怎么排序？
- 为什么不能只保存一个库存总数？

**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)
- [HotStatsServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/HotStatsServiceImpl.java)

原理或主题来源：[S17](sources.md#s17)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q020"></a>
## Q020 Lua 原子性指什么？脚本报错会像数据库一样回滚吗？

主题：Redis与锁；优先级：P0；状态：todo。

**参考回答**

脚本执行时其他客户端命令不能插入其中，可将校验与修改合成一段服务端操作并减少网络往返。但它不包括 MySQL；脚本运行时错误也不会自动撤销之前写入。应校验 key 类型与参数、先校验后写，并限制脚本长度。原子执行、错误回滚和跨系统一致性是不同承诺。

**继续追问**

- Pipeline 能替代吗？
- MULTI/EXEC 的运行错误怎样处理？

**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`RESERVE_SCRIPT`

原理或主题来源：[S05](sources.md#s05)、[S06](sources.md#s06)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q021"></a>
## Q021 预约脚本为什么先查资源占用再查库存？返回值有哪些？

主题：Redis与锁；优先级：P0；状态：todo。

**参考回答**

先查具体 seat 是否已占用，可以返回更明确的重复预约原因，再判断总体数量。当前不存在库存 key 返回 -3，占位冲突 -1，数量不足 -2，扣数并加入占位成功返回 1。业务层将这些结果映射成异常，顺序也影响用户看到的提示；不能用库存不足笼统描述每一种失败。

**继续追问**

- 部分 seat 可用是否应该部分下单成功？
- 重复 seatId 在进入脚本前如何处理？

**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`tryReserve`
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`distinct`

原理或主题来源：[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q022"></a>
## Q022 库存缓存初始化为什么可能出现半初始化？

主题：Redis与锁；优先级：P0；状态：todo。

**参考回答**

代码先 SET 库存数，再查明细写占位 Set。其他请求只检查库存 key 是否存在，可能在 Set 尚未构建时绕开初始化锁；中间崩溃也会留下不完整状态。可以研究数据准备后再原子发布 ready/version，并与在途预约协调。简单地双重检查库存 key 不能证明两份数据整体已就绪。

**继续追问**

- 初始化从 DB 读到的快照可能过时吗？
- 重建时如何保留未提交预占？

**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`ensureStockCache`

原理或主题来源：[S05](sources.md#s05)、[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q023"></a>
## Q023 释放脚本按 SREM 回补就代表任何补偿都安全了吗？

主题：Redis与锁；优先级：P0；状态：todo。

**参考回答**

按实际移除数回补可避免对同一占位直接重复加数。但当前只用 seatId 标识占位，没有预占订单或版本。旧补偿晚到时若同资源已被新请求占用，可能误移新占用；进程退出或 Redis 故障也会跳过释放。需要预占凭证归属、幂等补偿事件、有限重试与对账，而非只重复执行 release。

**继续追问**

- 没有订单 ID 的预扣怎样关联后续订单？
- 补偿记录以什么为事实来源？

**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`RELEASE_SCRIPT`

原理或主题来源：[S05](sources.md#s05)、[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q024"></a>
## Q024 Lua、Redisson、数据库唯一索引为何同时存在？

主题：Redis与锁；优先级：P0；状态：todo。

**参考回答**

Lua 在 Redis 做准入与预占，资源锁协调相同场次资源的数据库临界区，唯一索引对最终有效占位兜底。每层保护范围不同，也增加调用与故障成本。可以讨论在完整预占协议下是否缩减冗余锁，但不能凭其中一个机制就断言所有跨系统并发正确，尤其共享库存计数与状态迁移要单独设计。

**继续追问**

- 如果 Redis 已预占，同 seat 的锁还有多大作用？
- 只用数据库条件和唯一索引是否可行？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`lockSeats`
- [schema.sql](../../src/main/resources/db/schema.sql)：`uk_item_session_seat_active`

原理或主题来源：[S05](sources.md#s05)、[S09](sources.md#s09)、[S07](sources.md#s07)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q025"></a>
## Q025 Redisson 看门狗怎样工作？项目所有锁都会续期吗？

主题：Redis与锁；优先级：P0；状态：todo。

**参考回答**

未指定固定 leaseTime 的资源 RLock 可使用看门狗续期，由客户端维持仍持有的锁。初始化和缓存重建的 tryLock 指定了 5 秒 leaseTime，不能说所有锁都自动续期。锁有持有者线程和重入语义，解锁前验证 isHeldByCurrentThread。长 GC 或网络隔离仍可能让续期失败，不能替代数据库最终约束。

**继续追问**

- 为什么不是收到请求就固定设置超长 TTL？
- 进程退出后谁来 unlock？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`tryLock`
- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`INIT_LOCK_LEASE_SECONDS`

原理或主题来源：[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q026"></a>
## Q026 把 Redis 换成 Cluster，预约 Lua 能原样运行吗？

主题：Redis与锁；优先级：P1；状态：todo。

**参考回答**

不能直接保证。脚本同时操作库存与占位 Set，Cluster 要求相关 key 位于同一 hash slot。当前 key 名没有共享字面 hash tag。可把 key 改为 reserve:session:{1}:stock 和 reserve:session:{1}:seats，但还要设计旧 key 迁移、预占重建和发布兼容。使用分布式锁不意味着已经测试了 Cluster。

**继续追问**

- 为什么文档里的变量花括号不一定是真 hash tag？
- 把所有库存都放一个槽有什么问题？

**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`buildKeys`

原理或主题来源：[S08](sources.md#s08)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q027"></a>
## Q027 Redis 故障切换或锁租约到期后，旧持有者还继续执行怎么办？

主题：Redis与锁；优先级：P1；状态：todo。

**参考回答**

锁的互斥依赖存储、网络与租约条件，主从异步复制或长暂停可能造成旧持有者恢复时已失去资格。对关键写入要有数据库条件和唯一约束。更强协议可引入单调 fencing token，要求下游拒绝较旧版本，但当前没有实现。普通 RLock 不等于 Redlock，更不能用一个名词承诺任意故障下绝对安全。

**继续追问**

- 锁过期与业务停止是否同时发生？
- 为什么解锁不能直接 DEL 任意 key？

**源码入口**

- [RedissonConfig.java](../../src/main/java/com/jxbg/ticket/config/RedissonConfig.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`releaseLocks`

原理或主题来源：[S08](sources.md#s08)、[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q028"></a>
## Q028 项目使用哪种缓存读写方式，是否已经用逻辑过期？

主题：缓存；优先级：P0；状态：todo。

**参考回答**

场馆详情采用 Cache Aside：先读 Redis，未命中查 DB 并缓存，更新/停用后删除缓存。重建用互斥锁和二次检查，正常 TTL 30-35 分钟，空值 120-150 秒。当前没有逻辑过期后台刷新，也没有 Caffeine 两级缓存。新增场馆不主动清空可能已存在的空值缓存，应与源码事实一致。

**继续追问**

- 逻辑过期适合什么陈旧容忍度？
- 为什么不用直接更新缓存代替删除？

**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`getVenueDetail`

原理或主题来源：[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q029"></a>
## Q029 缓存空值怎样缓解穿透，为什么 TTL 比正常值短？

主题：缓存；优先级：P0；状态：todo。

**参考回答**

不存在的场馆也缓存空字符串，重复查相同 ID 不再每次访问 DB。短 TTL 减少后来新建或修复数据被长时间隐藏。它只能缓解重复不存在的 key，攻击者每次换新 ID 仍可穿透；要配合输入约束、限流和按需评估布隆过滤器。布隆过滤器尚未集成，不能把方案写成现成功能。

**继续追问**

- 空字符串和 Redis GET null 如何区分？
- 新建数据命中旧空值怎么办？

**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`CACHE_NULL_VALUE`

原理或主题来源：[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q030"></a>
## Q030 互斥重建怎样缓解击穿，目前等待策略有什么边界？

主题：缓存；优先级：P0；状态：todo。

**参考回答**

热点 key 过期后只让持锁者查询 DB 并回填，拿锁后再次查缓存避免重复重建。当前锁有固定 5 秒租约，拿不到时睡 50ms 后递归读取，没有明确重试次数上限。慢 DB 可能超过租约，递归与等待也会占线程；可研究有界循环、超时、旧值降级或请求合并。

**继续追问**

- 缓存雪崩为何不能只靠互斥锁？
- 查询失败时返回旧值是否可接受？

**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`rebuildVenueDetailCacheWithMutex`

原理或主题来源：[S09](sources.md#s09)、[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q031"></a>
## Q031 先更新数据库再删除缓存，还有哪些不一致窗口？

主题：缓存；优先级：P0；状态：todo。

**参考回答**

DB 更新成功后删缓存可能失败；另一个读请求也可能已读到旧 DB 值，在删除之后才回填旧缓存。当前写侧未和读侧重建锁统一协调，不能说已强一致。可按容忍度采用失效重试、版本校验或持久事件，延迟双删也只是特定场景下的缓解而非绝对证明。

**继续追问**

- 画出旧值回填的三步时序。
- 删缓存失败怎样留下可重试证据？

**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`updateVenue`

原理或主题来源：[C01](sources.md#c01)、[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q032"></a>
## Q032 Redis 不可用时是否应该所有请求直接查数据库？

主题：缓存；优先级：P0；状态：todo。

**参考回答**

应按业务分类。详情可考虑有限降级，但必须限制速率与并发，避免把 Redis 故障扩大成 DB 故障；当前并没有完整熔断方案。下单令牌、库存预扣和锁依赖 Redis，不能简单旁路。热度等可弱化，库存要先确认事实与恢复协议，不能全局清 key 作为通用修复。

**继续追问**

- 限流 Redis 异常放行后，下单一定成功吗？
- 重建库存为什么需要考虑在途请求？

**源码入口**

- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`isLimited`
- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)

原理或主题来源：[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q033"></a>
## Q033 JWT 与 submitToken 分别解决什么，是否可以互相替代？

主题：安全与幂等；优先级：P0；状态：todo。

**参考回答**

JWT 证明登录身份与角色，放请求头，当前默认有效 7200 秒。submitToken 是放请求体的一次性防重复提交随机值，Redis 保留 600 秒并通过 DEL 消费。登录可以多次调用接口，但一次提交动作只能消费一次令牌；两者不能互相替代，也不自动保证资源和订单归属安全。

**继续追问**

- JWT 的 payload 能被用户读取吗？
- 下单令牌是否应该存进 JWT？

**源码入口**

- [JwtUtil.java](../../src/main/java/com/jxbg/ticket/security/JwtUtil.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`generateSubmitToken`

原理或主题来源：[S13](sources.md#s13)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q034"></a>
## Q034 为什么消费下单令牌不用先 GET 再 DELETE？

主题：安全与幂等；优先级：P0；状态：todo。

**参考回答**

单条 DEL 会原子删除并返回是否删到 key。同一 token 并发请求只有一个能成功消费。先 GET 再 DEL 会让多个线程都先看到存在，形成检查与修改间隙。当前令牌消费后即使 DB 失败也不会自动恢复，所以这里的承诺是一次性准入，不是永久返回同一个订单。

**继续追问**

- 如何同时校验用户绑定再删除？
- 为什么检查 HTTP token 存在不能防重？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`checkAndConsumeSubmitToken`

原理或主题来源：[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q035"></a>
## Q035 服务端已下单但响应丢失，用户重试应该怎么办？

主题：安全与幂等；优先级：P0；状态：todo。

**参考回答**

当前旧 token 重试会冲突，不能凭异常推断没有订单。可先查询自己的订单确认，但当前对象归属也需加强。完整安全重试可以把 userId+idempotencyKey、请求摘要、处理状态和订单结果持久关联，相同请求返回原结果、相同 key 不同内容拒绝，并考虑处理中和超时恢复。这是改进设计。

**继续追问**

- 用户换 token 又提交有什么区别？
- 失败状态允许重试时如何避免双执行？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`checkAndConsumeSubmitToken`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q036"></a>
## Q036 当前 submitToken 绑定用户了吗，怎样改进？

主题：安全与幂等；优先级：P0；状态：todo。

**参考回答**

没有，当前 Redis value 为 1。知道令牌的其他请求不会被令牌机制本身按所有者拦截。可以把用户身份或提交上下文保存在令牌值中，用 Lua 原子检查匹配再删除，并从 SecurityContext 取身份。若希望请求重试返回结果，还需记录请求摘要与订单映射，不能只加 userId 前缀就说完整幂等。

**继续追问**

- 令牌是否应该放 URL？
- 如何避免日志记录完整令牌？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`generateSubmitToken`

原理或主题来源：[S05](sources.md#s05)、[S12](sources.md#s12)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q037"></a>
## Q037 项目有角色权限，为何还需要验证订单属于当前用户？

主题：安全与幂等；优先级：P0；状态：todo。

**参考回答**

角色规则只能决定某类用户能否进入接口，不能决定能否操作这个具体订单。当前订单详情、支付、取消等按 ID 操作，未全面核对 JWT 用户与订单所有者，列表外部 userId 也不可信。应从登录上下文取身份，在查询/写入加入归属条件，管理员跨用户权限单独定义，并用两个普通用户做越权回归。

**继续追问**

- UUID 订单号能代替权限校验吗？
- checkerId 应来自请求还是登录上下文？

**源码入口**

- [SecurityConfig.java](../../src/main/java/com/jxbg/ticket/config/SecurityConfig.java)
- [ReservationOrderController.java](../../src/main/java/com/jxbg/ticket/controller/ReservationOrderController.java)：`detail`
- [CheckinController.java](../../src/main/java/com/jxbg/ticket/controller/CheckinController.java)：`getCheckerId`

原理或主题来源：[S12](sources.md#s12)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q038"></a>
## Q038 禁用用户或修改角色后，旧 JWT 会立即失效吗？

主题：安全与幂等；优先级：P1；状态：todo。

**参考回答**

当前过滤器直接信任已验签 token 中的角色声明，不逐次查禁用状态，旧 JWT 可能在过期前继续有效。可按需求引入撤销信息、tokenVersion、短 token 与刷新机制，但需要额外存储、失效传播与可用性取舍。不能因数据库 status=0 就断言全部已登录会话已撤销。

**继续追问**

- 强制下线会不会削弱无状态优势？
- WebSocket 已建立连接怎样处理过期？

**源码入口**

- [JwtAuthenticationFilter.java](../../src/main/java/com/jxbg/ticket/security/JwtAuthenticationFilter.java)：`doFilterInternal`

原理或主题来源：[S12](sources.md#s12)、[S13](sources.md#s13)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q039"></a>
## Q039 BCrypt、JWT 签名、HTTPS 分别保护什么？

主题：安全与幂等；优先级：P1；状态：todo。

**参考回答**

BCrypt 用带盐自适应哈希保存密码并做 matches 校验，不用于解密；JWT 签名防篡改，不等于 payload 保密；HTTPS 保护传输中的机密性和完整性。三者不能互相替代。生产密钥应随机且可管理轮换，不能使用公开示例密钥或把真实 JWT 放 README。

**继续追问**

- 为什么同一密码两次 BCrypt 哈希不同？
- 把认证改成 Cookie 后是否还可无条件关闭 CSRF？

**源码入口**

- [SecurityConfig.java](../../src/main/java/com/jxbg/ticket/config/SecurityConfig.java)：`passwordEncoder`
- [AuthController.java](../../src/main/java/com/jxbg/ticket/controller/AuthController.java)：`matches`

原理或主题来源：[S12](sources.md#s12)、[S13](sources.md#s13)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q040"></a>
## Q040 为什么 Apifox 显示 HTTP 200，但业务仍然是失败？

主题：安全与幂等；优先级：P0；状态：todo。

**参考回答**

全局异常处理通常直接返回 ApiResult，没有同步设置协议状态，所以 HTTP 200 内可能有业务 code=409、429 或 500。安全链的 401/403 则显式设置了状态。接口断言、压测成功率与监控应同时检查两层，不能只看绿色通过或 HTTP 状态。是否统一为标准 HTTP 状态需明确接口兼容性。

**继续追问**

- 前端 Axios 拦截器需要怎么判断？
- 限流统计只统计 HTTP 429 会漏什么？

**源码入口**

- [GlobalExceptionHandler.java](../../src/main/java/com/jxbg/ticket/exception/GlobalExceptionHandler.java)
- [SecurityConfig.java](../../src/main/java/com/jxbg/ticket/config/SecurityConfig.java)：`writeJson`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q041"></a>
## Q041 延迟消息里面是什么，过期之后谁处理？

主题：消息与调度；优先级：P0；状态：todo。

**参考回答**

消息是订单 ID。它进入无业务消费者的延迟队列，队列消息 TTL 到期后通过 DLX 转发关单队列，由监听该队列的消费者查 DB 最新状态。只有待支付且确已过期才尝试关单。不是消息自己执行订单逻辑，也不是保存创建时的未支付状态到期直接照做。

**继续追问**

- 支付成功后还要删除延迟消息吗？
- 为什么消费者必须再查 expire_time？

**源码入口**

- [OrderTimeoutRabbitConfig.java](../../src/main/java/com/jxbg/ticket/config/OrderTimeoutRabbitConfig.java)
- [OrderTimeoutMessageConsumer.java](../../src/main/java/com/jxbg/ticket/mq/OrderTimeoutMessageConsumer.java)：`closeTimeoutOrder`

原理或主题来源：[S14](sources.md#s14)、[S15](sources.md#s15)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q042"></a>
## Q042 Publisher confirm 与 consumer ACK 有什么区别？当前做到了哪一步？

主题：消息与调度；优先级：P0；状态：todo。

**参考回答**

confirm 关注 broker 对发布的接收确认，consumer ACK 关注消费端是否完成处理。当前生产者 convertAndSend，未显式完成 confirm/returns 处理；监听方法依赖 Spring 容器默认自动确认而非手写 ACK。durable 队列只是基础，还需核对消息持久属性、故障恢复和队列类型，不能说这些配置保证绝不丢消息。

**继续追问**

- 已消费成功但确认前掉线会怎样？
- 消息到交换机却路由不到队列怎么办？

**源码入口**

- [RabbitOrderTimeoutMessageProducer.java](../../src/main/java/com/jxbg/ticket/mq/RabbitOrderTimeoutMessageProducer.java)
- [OrderTimeoutMessageConsumer.java](../../src/main/java/com/jxbg/ticket/mq/OrderTimeoutMessageConsumer.java)

原理或主题来源：[S16](sources.md#s16)、[C04](sources.md#c04)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q043"></a>
## Q043 重复收到关单消息，怎样避免重复释放库存？

主题：消息与调度；优先级：P0；状态：todo。

**参考回答**

当前 closeTimeoutOrder 先看状态和到期，再条件 UPDATE PENDING 到 CANCELLED，只有更新成功才释放有效明细与库存。Redis release 又按实际移除数回补。两层对重复关单有帮助，但不同订单的 DB 库存回补和支付竞争仍需原子设计。幂等要覆盖副作用，不只是方法第二次不抛异常。

**继续追问**

- 消息重复与两个定时任务重复扫描有何相似？
- 一个消费者执行完 DB、未释放 Redis 就退出怎么办？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrder`
- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`RELEASE_SCRIPT`

原理或主题来源：[S16](sources.md#s16)、[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q044"></a>
## Q044 为什么事务提交后发 MQ 还会丢事件，Outbox 改善什么？

主题：消息与调度；优先级：P0；状态：todo。

**参考回答**

提交后发送避免消费到未提交订单，但 DB 成功后、发送前进程仍可能退出，网络超时也可能让接收结果不确定。当前捕获发送异常并保留扫描兜底。Outbox 让订单与待发事件同 DB 事务提交，由任务重试投递并记录结果，消费者仍需幂等。项目尚无 Outbox，不能将建议描述为当前可靠消息协议。

**继续追问**

- Outbox 标记发送成功前退出会发生什么？
- 只打开 confirm 能消除本地提交窗口吗？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`sendCloseOrderMessageAfterCommit`

原理或主题来源：[S03](sources.md#s03)、[S16](sources.md#s16)、[C04](sources.md#c04)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q045"></a>
## Q045 TTL 到期为什么不保证订单刚好 15 分钟关闭？

主题：消息与调度；优先级：P1；状态：todo。

**参考回答**

TTL 只使消息满足过期处理条件，转发、调度、积压和依赖延迟影响真正执行时间。当前使用统一队列消息 TTL，不是每单任意延迟，改已有队列参数也可能声明冲突。混用不同消息 TTL 还需考虑队头阻塞。消费者必须核对数据库过期时间，定时扫描保证有机会补偿而非毫秒级实时。

**继续追问**

- 应用时钟不一致怎么办？
- 把订单过期时间改短但消息 TTL 不变会怎样？

**源码入口**

- [OrderTimeoutRabbitConfig.java](../../src/main/java/com/jxbg/ticket/config/OrderTimeoutRabbitConfig.java)：`orderDelayQueue`
- [application.yml](../../src/main/resources/application.yml)：`close-delay-ms`

原理或主题来源：[S14](sources.md#s14)、[S15](sources.md#s15)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q046"></a>
## Q046 应用部署两个实例，@Scheduled 会只执行一次吗？

主题：消息与调度；优先级：P1；状态：todo。

**参考回答**

不会，每个实例都会按本地调度配置运行。可用业务条件更新保证处理资格，必要时增加任务协调锁、分片或独立调度平台。当前项目没有 XXL-JOB；扫描整批在一个事务中也会影响锁持有与吞吐。需要区分调度只触发一次、任务容忍重复和每条业务只生效一次。

**继续追问**

- 锁过期但任务没完成会怎样？
- 扫描和 MQ 同时关单如何协调？

**源码入口**

- [OrderTimeoutJob.java](../../src/main/java/com/jxbg/ticket/job/OrderTimeoutJob.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrders`

原理或主题来源：[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q047"></a>
## Q047 订单长期不关闭或消费积压，如何定位？

主题：消息与调度；优先级：P1；状态：todo。

**参考回答**

先看 MQ 开关、生产日志与路由，区分消息未发、在延迟队列、进入关闭队列还是消费失败。观察 ready/unacked、消费速率、重投递、最老过期订单延迟、DB 锁和连接等待。扫描默认每分钟一个 50 条批次也可能追不上积压。扩消费者前确认业务幂等与 DB 容量，不直接无限重试异常消息。

**继续追问**

- 非法 orderId 当前如何处理？
- 如何证明某单是 MQ 而非扫描关闭？

**源码入口**

- [OrderTimeoutMessageConsumer.java](../../src/main/java/com/jxbg/ticket/mq/OrderTimeoutMessageConsumer.java)
- [OrderTimeoutJob.java](../../src/main/java/com/jxbg/ticket/job/OrderTimeoutJob.java)

原理或主题来源：[S16](sources.md#s16)、[C04](sources.md#c04)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q048"></a>
## Q048 AOP 操作日志为何使用 REQUIRES_NEW，是否一定反映最终成功？

主题：AOP与限流；优先级：P1；状态：todo。

**参考回答**

独立事务可以使失败操作的审计记录不随业务回滚，但也要额外借连接，且审计 SUCCESS 的写入时机未必在外层最终提交之后。核销 Controller 同时有事务和日志切面，需核对切面包裹顺序。核销失败记录则在原事务里写后抛错，可能一同回滚。日志类型与事务语义要分别说明。

**继续追问**

- 独立事务为什么可能加重连接池等待？
- 日志写失败应该阻断主业务吗？

**源码入口**

- [OperationLogServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/OperationLogServiceImpl.java)：`REQUIRES_NEW`
- [CheckinController.java](../../src/main/java/com/jxbg/ticket/controller/CheckinController.java)：`saveFailedRecord`

原理或主题来源：[S02](sources.md#s02)、[S04](sources.md#s04)、[C02](sources.md#c02)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q049"></a>
## Q049 OperationLogContext 为什么要在 finally 中清理 ThreadLocal？

主题：AOP与限流；优先级：P1；状态：todo。

**参考回答**

ThreadLocal 隔离的是线程上的值，HTTP 线程会复用，未清理可能让下一请求读取上次业务 ID，并造成对象长期被线程引用。finally 覆盖成功与失败路径。普通 ThreadLocal 也不会自动传到异步线程，因此异步审计要显式传必要上下文并控制生命周期，不能把当前安全上下文放全局变量。

**继续追问**

- 弱引用为什么不代表永远不会内存泄漏？
- 线程池传播用户身份怎样避免串用户？

**源码入口**

- [OperationLogContext.java](../../src/main/java/com/jxbg/ticket/common/OperationLogContext.java)
- [OperationLogAspect.java](../../src/main/java/com/jxbg/ticket/aspect/OperationLogAspect.java)：`clear`

原理或主题来源：[S23](sources.md#s23)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q050"></a>
## Q050 当前限流是什么算法，INCR 与 EXPIRE 分开有什么风险？

主题：AOP与限流；优先级：P0；状态：todo。

**参考回答**

当前是首次请求建立 TTL 的固定窗口计数。INCR 后仅 count=1 时 EXPIRE，两步之间退出可留下无 TTL key。可用 Lua 合并，但仍需说明窗口边界突刺。它不是滑动窗口或令牌桶。按实际 URI 构造 key 还会把不同订单支付拆成不同计数，是否符合用户总频率限制要核对需求。

**继续追问**

- 阈值附近跨窗口能突发多少请求？
- 如何按路由模板而非订单 ID 限流？

**源码入口**

- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`isLimited`
- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`buildRateLimitKey`

原理或主题来源：[S18](sources.md#s18)、[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q051"></a>
## Q051 限流 Redis 异常时放行、直接信任 X-Forwarded-For，有何取舍？

主题：AOP与限流；优先级：P1；状态：todo。

**参考回答**

当前计数异常会放行，偏可用性，称 fail-open；高风险入口可能需保守拒绝或本地保护，但不能一概牺牲可用性。转发头只有来自可信代理并被覆盖时才可靠，直接读取第一个 IP 会被伪造。还要注意共用 NAT 用户和代理链，用户级、IP 级与资源级约束各有目的。

**继续追问**

- 登录接口怎么限流才不误伤校园网？
- 应用后续仍依赖 Redis 时放行有什么实际收益？

**源码入口**

- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`getClientIp`

原理或主题来源：[S18](sources.md#s18)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q052"></a>
## Q052 点赞为什么使用 Redis Set 加数据库唯一索引，取消时如何避免计数漂移？

主题：点评与通知；优先级：P0；状态：todo。

**参考回答**

Set 做快速会员判断，数据库有效点赞唯一索引做最终事实去重，计数用 SQL 增减。Redis 丢失时 DB 仍能拦重复。当前取消是先查再改，未全面通过影响行数决定唯一递减，两个并发取消可能导致计数偏差。应以成功取消有效记录为资格，再改计数并处理缓存失败，对账以有效点赞记录为准。

**继续追问**

- 计数限制不小于零是否就正确？
- 怎样处理 DB 成功但 SADD 失败？

**源码入口**

- [ReviewServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java)：`cancelLike`
- [ReviewMapper.java](../../src/main/java/com/jxbg/ticket/mapper/ReviewMapper.java)

原理或主题来源：[S07](sources.md#s07)、[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q053"></a>
## Q053 点评分页如何避免逐条查作者和图片的 N+1 问题？

主题：点评与通知；优先级：P1；状态：todo。

**参考回答**

先查询当前页，再收集点评 ID 和去重用户 ID，批量查询图片与用户，建 Map 后组装 VO。这样减少逐条关联请求，但仍有主查询、分页 count 与批量查询，不等于一条 SQL。要控制页大小和 IN 列表，避免为优化少量请求引入无限大批次。

**继续追问**

- 为什么作者 Map 的 key 用用户 ID？
- JOIN 一次查询是否一定更快？

**源码入口**

- [ReviewServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java)：`loadReviewImages`

原理或主题来源：[S21](sources.md#s21)、[S25](sources.md#s25)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q054"></a>
## Q054 热门场馆是如何排名的？它是严格的业务统计吗？

主题：点评与通知；优先级：P1；状态：todo。

**参考回答**

ZSet 以场馆或资源 ID 为 member，根据浏览、下单、支付、核销、点评设置权重增分。当前是累计热度，不是自动按日榜或衰减，也没有完整反刷。部分加分发生于 DB 提交前，回滚不能自动撤销，因此不能把分数当精确财务/订单报表；应明确可丢性、保留周期和重建规则。

**继续追问**

- 如何做日榜和衰减？
- 为什么总榜不能无限记录历史失效资源？

**源码入口**

- [HotStatsServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/HotStatsServiceImpl.java)：`VENUE_VIEW_SCORE`

原理或主题来源：[S17](sources.md#s17)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q055"></a>
## Q055 既然用 WebSocket，为什么还要存 user_notification？

主题：点评与通知；优先级：P0；状态：todo。

**参考回答**

长连接只能加速在线提示，断线与离线都可能错过，通知表保存可分页补查的事实与未读状态。当前 createAndPush 先保存再立即推送，但可能还处于外层事务内，不能把 save 返回当提交完成。改进为提交后推送并支持重连补查、按通知 ID 去重，必要时用持久事件重试。

**继续追问**

- 客户端收到推送而 DB 后来回滚怎么办？
- 未读数由谁作为事实来源？

**源码入口**

- [UserNotificationServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/UserNotificationServiceImpl.java)：`createAndPush`

原理或主题来源：[S19](sources.md#s19)、[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q056"></a>
## Q056 WebSocket 在 CONNECT 验 JWT 就完成安全控制了吗？

主题：点评与通知；优先级：P0；状态：todo。

**参考回答**

没有。CONNECT 绑定身份之后，还需限制 SUBSCRIBE/SEND 目标，防止订阅他人的底层队列或向未授权目标发消息，并考虑过期 token 与禁用用户。当前只在 CONNECT 拦截器绑定 Principal，尚无完整消息级权限。simple broker 在单进程内，多实例路由也需额外设计，不能凭 /user 前缀就保证所有目标安全。

**继续追问**

- 跨域 Origin 白名单是不是身份认证？
- 用户连接在实例 A，业务在实例 B 如何推送？

**源码入口**

- [WebSocketConfig.java](../../src/main/java/com/jxbg/ticket/config/WebSocketConfig.java)：`configureClientInboundChannel`

原理或主题来源：[S19](sources.md#s19)、[S12](sources.md#s12)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q057"></a>
## Q057 上传只检查 jpg/png 扩展名够吗，项目做了哪些检查？

主题：工程安全；优先级：P1；状态：todo。

**参考回答**

当前有大小限制、扩展名白名单、随机文件名和路径规范化，但扩展名可伪造。工程上还可做文件签名/解码验证、像素与解压炸弹限制、归属鉴权、内容审核、不可执行存储和配额。文件系统写入不受 DB 事务自动回滚，点评取消或提交失败还涉及孤立文件清理。当前未集成对象存储。

**继续追问**

- 多实例本地上传为何访问不到同一文件？
- 如何防止路径穿越和任意图片 URL 引用？

**源码入口**

- [FileUploadServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/FileUploadServiceImpl.java)：`validateImage`

原理或主题来源：[S20](sources.md#s20)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q058"></a>
## Q058 POI 导出几千行和几十万行有什么差别？

主题：工程性能；优先级：P1；状态：todo。

**参考回答**

当前限制导出规模，普通工作簿与一次读完整列表会占较多堆。大规模要评估分页读取、流式写入、临时文件清理、异步任务和下载权限，不能简单删掉上限。当前 setCellValue 写字符串，不应直接判定公式注入；若改 CSV 或自动识别公式再评估。导出必须从登录身份限制数据。

**继续追问**

- 流式写入是否意味着无内存成本？
- 导出期间数据变化怎样保证口径？

**源码入口**

- [ExcelExportServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ExcelExportServiceImpl.java)
- [ExcelExportController.java](../../src/main/java/com/jxbg/ticket/controller/ExcelExportController.java)

原理或主题来源：[S26](sources.md#s26)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q059"></a>
## Q059 怎样验证项目的并发正确性，而不只是拿到一个 QPS 数字？

主题：测试与排障；优先级：P0；状态：todo。

**参考回答**

分别测试同资源竞争、不同资源共享库存、支付与关单、重复提交及故障补偿。使用独立 token 与统一起跑，检查业务 code、有效明细唯一性、DB/Redis 库存、最终状态和副作用数量；限流拒绝单列。性能记录环境、版本、数据量、持续时间、成功率和尾延迟，没有数据就不宣称支持多少并发。

**继续追问**

- 只用一个 submitToken 压测会测成什么？
- 顺序循环请求为何不是并发竞争测试？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)
- [check-invariants.sql](../../docs/learning/labs/check-invariants.sql)

原理或主题来源：[S22](sources.md#s22)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q060"></a>
## Q060 用户说资源一直不可预约，你按什么顺序排查？

主题：测试与排障；优先级：P0；状态：todo。

**参考回答**

先确认 sessionId、seatId、时间与业务响应；查有效 order_item 和对应订单是待支付、已支付、已取消还是已核销，再查到期与关单日志，最后比较 Redis 数量和占位。区分正常占用、过期未释放和数据漂移。seat.status 是全局启停，不能为了某场次恢复而随意改它，也不要清空全部 Redis。

**继续追问**

- 订单 CANCELLED 但 Redis 仍占位意味着什么窗口？
- 如何留下修复前后的证据？

**源码入口**

- [SessionSeatController.java](../../src/main/java/com/jxbg/ticket/controller/SessionSeatController.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`releaseRedisStockAfterCommit`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q061"></a>
## Q061 金额为什么用 BigDecimal，equals 与 compareTo 有什么区别？

主题：Java基础；优先级：P1；状态：todo。

**参考回答**

十进制计费用 BigDecimal 避免二进制浮点近似引入的误差，注意不要从不精确 double 构造。equals 还比较 scale，compareTo 按数值比较；加法返回新对象。订单要固定舍入、精度和快照口径。用了 BigDecimal 并不代表折扣分摊、溢出校验和退款规则已经实现。

**继续追问**

- 0.1+0.2 的 double 结果能直接用于金额吗？
- 数据库 DECIMAL 精度不足怎样处理？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`BigDecimal`
- [schema.sql](../../src/main/resources/db/schema.sql)：`DECIMAL`

原理或主题来源：[S24](sources.md#s24)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q062"></a>
## Q062 volatile、synchronized 和 Redis 锁能互相替换吗？

主题：Java基础；优先级：P1；状态：todo。

**参考回答**

volatile 主要约束可见性与顺序，不把 count++ 变成原子；synchronized/ReentrantLock 协调当前 JVM 的临界区；Redis 锁用于不同应用实例共享协调，仍有租约与故障边界。数据库条件写入又是另一层最终约束。先说明共享状态在哪里，再选机制，不能用一个关键词覆盖全部并发问题。

**继续追问**

- 两台应用各自 synchronized 有效吗？
- happens-before 是否等于墙上时钟先发生？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`lockSeats`

原理或主题来源：[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q063"></a>
## Q063 seatIds 去重排序、批量查询结果建 Map 有什么意义？

主题：Java基础；优先级：P1；状态：todo。

**参考回答**

去重防止一个请求重复计算同资源数量，排序为多资源锁提供统一顺序。用 ID 建 Map 能高效把批量查回的作者、图片或资源与原顺序关联，避免依赖数据库 IN 查询返回顺序。Long 比值不要用 ==，可变 HashMap 也不因使用在 Spring 单例里就线程安全。

**继续追问**

- HashMap 哈希冲突怎样处理？
- Stream.toList 的结果能直接 add 吗？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`sorted`
- [ReviewServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java)：`toMap`

原理或主题来源：[S27](sources.md#s27)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q064"></a>
## Q064 LocalDateTime、过期时间与随机订单号有什么工程边界？

主题：Java基础；优先级：P1；状态：todo。

**参考回答**

LocalDateTime 不自带时区，跨服务与数据库要统一时钟和存储口径，避免把格式一致当实际时刻一致。TTL 与 DB expire_time 也需协调。当前订单号是时间加 UUID 片段，不是雪花算法；它降低碰撞概率，DB 唯一约束才最终拦冲突。不要以时间戳天然唯一作为保证。

**继续追问**

- 时钟回拨对排序和过期有何影响？
- 若订单号碰撞应如何返回和重试？

**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`generateOrderNo`
- [application.yml](../../src/main/resources/application.yml)：`expire-minutes`

原理或主题来源：[S07](sources.md#s07)、[S14](sources.md#s14)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q065"></a>
## Q065 别人克隆仓库如何启动，哪些配置绝不能直接公开部署？

主题：部署与运维；优先级：P0；状态：todo。

**参考回答**

按 README 准备 JDK21/Maven/MySQL/Redis，导入 schema，复制本地示例配置并设置凭据与随机 JWT_SECRET，运行 Boot 或构建 JAR。MQ 默认可关闭。真实本地配置、日志、上传文件和构建产物已排除 Git；公开示例账号只用于演示。代码上传与服务安全部署是两件不同工作。

**继续追问**

- 环境变量和配置文件优先级如何核对？
- Compose 是启动后端还是只启动中间件？

**源码入口**

- [README.md](../../README.md)
- [application-local.example.yml](../../src/main/resources/application-local.example.yml)
- [.gitignore](../../.gitignore)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q066"></a>
## Q066 一次业务故障需要记录哪些日志与指标？

主题：部署与运维；优先级：P1；状态：todo。

**参考回答**

至少有时间、请求关联 ID、业务 ID、接口与角色、耗时、业务错误码及必要堆栈；跨 MQ 传播关联信息便于查生产与消费。监控同时看业务失败、连接等待、最老超时订单和库存不一致，不能只看 HTTP 5xx。当前未完整集成链路追踪，日志也不记录原始密码/JWT。

**继续追问**

- 操作日志与运行日志有何区别？
- 怎么避免用户输入污染日志或泄密？

**源码入口**

- [GlobalExceptionHandler.java](../../src/main/java/com/jxbg/ticket/exception/GlobalExceptionHandler.java)
- [OperationLogAspect.java](../../src/main/java/com/jxbg/ticket/aspect/OperationLogAspect.java)

原理或主题来源：[S23](sources.md#s23)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q067"></a>
## Q067 接口慢时，把线程池和数据库连接池调大就能解决吗？

主题：工程性能；优先级：P1；状态：todo。

**参考回答**

不一定。应先区分 CPU、GC、锁等待、数据库、Redis 和网络瓶颈。更多线程可能只在同一 DB 容量前排队，增加堆、切换和尾延迟；连接过多也会压垮 DB。需要有界队列、超时、拒绝策略和观测。Java21 不代表当前已启用虚拟线程，parallelStream 也不能随意用于依赖线程事务的写入。

**继续追问**

- REQUIRES_NEW 对连接数有什么影响？
- 为什么 P99 上升而均值变化不大？

**源码入口**

- [pom.xml](../../pom.xml)：`java.version`
- [OperationLogServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/OperationLogServiceImpl.java)：`REQUIRES_NEW`

原理或主题来源：[S02](sources.md#s02)、[S22](sources.md#s22)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q068"></a>
## Q068 schema.sql 加一列，线上数据库重跑就自动升级了吗？

主题：部署与运维；优先级：P1；状态：todo。

**参考回答**

不会。CREATE TABLE IF NOT EXISTS 不会替已有表执行结构迁移。应维护版本化变更、备份、验证脚本与应用兼容顺序，考虑索引变更成本和回滚。Redis key 格式与 MQ 队列参数也是版本协议，不能只更新 Java 配置。当前未集成 Flyway/Liquibase，不能把它们列成已用技术。

**继续追问**

- 滚动发布时新旧实例字段不同怎么兼容？
- 什么时候回滚应用不能回滚数据？

**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)
- [OrderTimeoutRabbitConfig.java](../../src/main/java/com/jxbg/ticket/config/OrderTimeoutRabbitConfig.java)

原理或主题来源：[S07](sources.md#s07)、[S14](sources.md#s14)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q069"></a>
## Q069 怎样严谨地写缓存优化与压测成果？

主题：测试与排障；优先级：P0；状态：todo。

**参考回答**

报告记录提交、硬件、JVM、中间件、数据量、冷热缓存、并发、持续时间和限流配置，对比业务成功率、吞吐与 P50/P95/P99，再看 DB 查询数、CPU/GC 与依赖延迟。不要把错误请求快返回当性能提升，也不把单机结果推导到任意生产环境。没有自己的报告就只写机制，不编造提升比例和 QPS。

**继续追问**

- 为什么要预热？
- 固定并发与固定到达速率结果为何不同？

**源码入口**

- [lab-record.md](../../docs/learning/templates/lab-record.md)
- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)

原理或主题来源：[S22](sources.md#s22)、[S21](sources.md#s21)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q070"></a>
## Q070 面试官问篮球场活动为何能看到讲座座位，怎么回答？

主题：项目设计；优先级：P1；状态：todo。

**参考回答**

当前资源主要按同馆查询，活动、场次、区域和可选资源尚未建立完整绑定，演示数据因此可能混在一起。应诚实指出实现范围，而不是事后编一个未实现规则。可改进为场次资源关联或区域约束，校验库存初始化、查询和下单共用相同范围，并明确价格规则的匹配顺序。

**继续追问**

- 只改展示列表，不改下单校验有什么漏洞？
- 资源停用后已支付订单怎么办？

**源码入口**

- [SessionSeatController.java](../../src/main/java/com/jxbg/ticket/controller/SessionSeatController.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`getVenueId`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q071"></a>
## Q071 如何保证学习资料后来能增删改查且不与源码脱节？

主题：学习与设计；优先级：P1；状态：todo。

**参考回答**

按主题拆 Markdown，以 questions.json 为题库唯一数据源，每题保存稳定 ID、代码方法、来源、优先级、状态和个人笔记。维护脚本验证引用、备份变更并生成阅读版，Git 保留历史。每次改业务后重新核对相关方法和实验，不把路径存在检查当技术正确性证明。

**继续追问**

- 直接编辑生成的题集为什么会丢改动？
- 哪些笔记不适合推送公开仓库？

**源码入口**

- [study.py](../../tools/study.py)：`StudyStore`
- [README.md](../../docs/learning/README.md)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q072"></a>
## Q072 这个项目哪些设计思想可迁移，哪些能力暂时不能宣称？

主题：学习与设计；优先级：P0；状态：todo。

**参考回答**

可迁移的是业务不变量、数据库约束、条件状态迁移、缓存可重建、提交后副作用、幂等与补偿、身份与对象授权分离，以及用实验验证故障假设。当前不能宣称生产高可用、多实例高并发验证、完整消息可靠性、跨系统强一致或真实支付。面试应把已实现、待验证和改进方案分别讲清。

**继续追问**

- 只允许改一个地方，怎样按影响选择优先级？
- 为什么增加更多中间件未必更有工程价值？

**源码入口**

- [12-implementation-boundaries.md](../../docs/learning/12-implementation-boundaries.md)
- [OrderStateMachine.java](../../src/main/java/com/jxbg/ticket/service/OrderStateMachine.java)

**我的笔记**

待补充自己的推演、实验记录和回答。
