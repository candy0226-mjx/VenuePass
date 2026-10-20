# VenuePass 面试题集

本文件由 `python tools/study.py export` 生成。请编辑 questions.json 或使用维护工具。

按“面试官提问 → 我回答 → 两轮追问与回答”阅读。回答是口语示范，请结合自己的理解和真实经历表达，不要把改进方案说成已实现。

源码核对与修订日期见 questions.json 的 updated_at；原理来源见 sources.md。优先级为学习顺序，不代表真实面试概率。

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

**面试官：** 用一分钟说明 VenuePass 解决什么问题、采用什么架构。

**我：**

这个项目做的是场馆分时预约。比如用户选一个晚上的场次，再选篮球场或座位，接着下单、模拟支付、凭码入场，核销后还能写图文点评。

我采用 Spring Boot 单体后端，MySQL 保存订单等正式数据，Redis 做缓存、防重复提交和库存预扣，RabbitMQ 提供延迟关单的处理代码。项目重点是把预约流程和并发控制串起来，目前没有可引用的高并发压测报告，MQ 真实环境验证也还要补。

**面试官追问 1：** 哪些数据必须一起成功或一起失败？

**我：** 下单时的订单、订单明细和数据库库存要放在同一个数据库事务里，否则可能出现有订单却没有占位。Redis 预扣不在这个事务里，需要单独考虑失败补偿。

**面试官追问 2：** 为什么没有拆成微服务？

**我：** 当前业务规模用单体更容易开发和排查。拆分后还会增加远程调用、分布式事务和部署成本。我会等模块边界和独立扩容需求明确后，再决定是否拆分。


**源码入口**

- [pom.xml](../../pom.xml)
- [TicketBootApplication.java](../../src/main/java/com/jxbg/ticket/TicketBootApplication.java)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q002"></a>
## Q002 sessionId、seatId、场次库存分别代表什么？

主题：项目设计；优先级：P0；状态：todo。

**面试官：** sessionId、seatId、场次库存分别代表什么？

**我：**

可以把 sessionId 理解成“预约哪一场”，seatId 理解成“预约哪个资源”。比如 sessionId=2 是晚上七点到八点这一场，seatId=2 是二号篮球场；这两个 2 只是恰好相同，没有特殊关联。

一次预约既要看这场还剩多少名额，也要看指定资源有没有被占。当前按 sessionId + seatId 限制有效占位，session 本身不是一张资源清单，资源查询目前主要按场馆筛选。

**面试官追问 1：** 为什么一个 sessionId 能查出很多 seatId？

**我：** 同一场可以开放多个资源，就像同一场电影里有很多座位。查询返回的是该场查询范围内的资源及可用状态，再由用户选择其中一个或几个。

**面试官追问 2：** 有四个资源、库存只有二，是不是数据错了？

**我：** 不一定，资源数量和可售配额可以不同。当前 available 同时检查数据库场次库存大于零、这个资源没有有效占位，但不会替用户锁住它。库存只有二时，四个尚未占用的资源仍可能都显示 true；一次选三个或查询后被别人抢先预约，下单仍可能失败。


**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)
- [SessionSeatController.java](../../src/main/java/com/jxbg/ticket/controller/SessionSeatController.java)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q003"></a>
## Q003 不同场次预约同一个资源一定不会时间冲突吗？

主题：项目设计；优先级：P0；状态：todo。

**面试官：** 不同场次预约同一个资源一定不会时间冲突吗？

**我：**

不一定。现在数据库限制的是“同一场次里的同一个资源”，两个不同场次即使时间重叠，也不会因为这个索引自动冲突。

如果业务要支持任意时间预约，我会增加时间重叠检查。例如已有预约开始时间小于新预约结束时间，并且已有结束时间大于新开始时间，就存在重叠。这个检查还必须和写入一起受并发控制，否则两个人可能同时查到没冲突、随后都写进去。这是待完善的设计。

**面试官追问 1：** 七点到八点和八点到九点算重叠吗？

**我：** 如果约定结束时刻不占用，也就是左闭右开区间，就不重叠。若还需要清场时间，就要把清场间隔也纳入判断，规则必须先确定。

**面试官追问 2：** 具体怎么防止同时通过时间检查？

**我：** 一种方案是按资源 ID 串行处理预约检查和写入，例如在事务里锁定对应资源行，再查重叠和插入。所有预约入口都要遵守这个规则，代价是同一资源上的请求需要等待。


**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)：`uk_item_session_seat_active`

原理或主题来源：[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q004"></a>
## Q004 为什么保存订单价格快照，价格规则现在真正参与计费了吗？

主题：项目设计；优先级：P1；状态：todo。

**面试官：** 为什么保存订单价格快照，价格规则现在真正参与计费了吗？

**我：**

我会把下单时的单价和总价保存下来。比如用户按 80 元下单，场馆第二天改成 100 元，旧订单仍然应该是 80 元。

当前下单代码累加的是 Seat.basePrice。项目虽然有价格规则表和管理接口，但下单还没有完整接入这些规则，所以现在不能说已经支持各种分时动态定价。后续接入时，还要明确规则优先级和金额舍入方式。

**面试官追问 1：** 如果多个资源一起使用一张优惠券，金额怎么分摊？

**我：** 这是扩展设计，可以按各项原价比例分摊，保留到分，最后一项承担舍入差额，保证明细合计等于实付金额。当前项目没有完整优惠券与退款流程。

**面试官追问 2：** 退款为什么不能按现在的资源价格算？

**我：** 因为退款针对的是那笔交易，应参考原订单实付金额、已退金额和退款规则。现在的售价不能代表用户当时付了多少。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`getBasePrice`
- [schema.sql](../../src/main/resources/db/schema.sql)：`price_rule`

原理或主题来源：[S24](sources.md#s24)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q005"></a>
## Q005 Controller、Service、Mapper 和 DTO/Entity/VO 为什么要区分？

主题：Spring与结构；优先级：P1；状态：todo。

**面试官：** Controller、Service、Mapper 和 DTO/Entity/VO 为什么要区分？

**我：**

我把它理解成职责分开：Controller 接请求和校验参数，Service 处理业务规则与事务，Mapper 负责访问数据库。DTO 是别人能传什么，Entity 是数据库存什么，VO 是接口要返回什么。

这样可以避免前端直接设置不该设置的字段，例如订单状态和创建时间。当前代码并不是每处都完全分开了，部分管理接口仍接收 Entity，核销逻辑也主要在 Controller，这些可以继续整理。

**面试官追问 1：** 前端传 status=PAID，后端直接保存不行吗？

**我：** 不行，支付状态应由服务端的支付流程决定。否则调用者修改一个参数就能跳过支付。后端应只接收允许修改的字段，并自己完成状态转换。

**面试官追问 2：** 核销放到 Service 有什么实际好处？

**我：** HTTP 接口和以后可能增加的其他入口可以复用同一套核销规则，事务边界也更集中。测试时能直接围绕业务输入和状态变化，不必依赖 Controller 请求环境。


**源码入口**

- [CreateOrderRequest.java](../../src/main/java/com/jxbg/ticket/dto/CreateOrderRequest.java)
- [OrderDetailVO.java](../../src/main/java/com/jxbg/ticket/vo/OrderDetailVO.java)
- [CheckinController.java](../../src/main/java/com/jxbg/ticket/controller/CheckinController.java)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q006"></a>
## Q006 从收到 POST /orders 到返回订单 ID，按源码顺序讲一次。

主题：Spring与结构；优先级：P0；状态：todo。

**面试官：** 从收到 POST /orders 到返回订单 ID，按源码顺序讲一次。

**我：**

我按一次正常下单来讲：先经过 JWT 认证和接口权限检查，参数通过校验后进入 Controller 的切面，再由 Service 事务代理调用下单方法。

方法先消费下单令牌，检查用户、场次和资源；然后用 Lua 预扣 Redis 库存并记录占位，再按资源排序加 Redisson 锁，检查数据库占位，保存订单和明细，回写数据库库存。数据库提交成功后尝试发送延迟关单消息，事务结束后释放锁。

这里特别要注意两个顺序：实际代码是先 Lua、后资源锁；方法体返回订单 ID，也不代表数据库事务此时已经提交。

**面试官追问 1：** 数据库提交失败，预扣库存怎么办？

**我：** 代码注册了事务完成回调，明确回滚时调用 Redis 释放脚本。但如果进程退出，或者事务结果不确定、补偿又失败，这个内存回调不能保证修复，需要额外对账。

**面试官追问 2：** 为什么不在创建订单之前就发送关单消息？

**我：** 消费者可能查不到还没提交的订单，也可能收到后来回滚的订单。提交后发送能避开这个问题，但提交成功到消息发出之间仍有失败窗口。


**源码入口**

- [ReservationOrderController.java](../../src/main/java/com/jxbg/ticket/controller/ReservationOrderController.java)：`create`
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`createOrder`

原理或主题来源：[S01](sources.md#s01)、[S03](sources.md#s03)、[S12](sources.md#s12)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q007"></a>
## Q007 单例 Service 是否线程安全？构造器注入解决了什么？

主题：Spring与结构；优先级：P1；状态：todo。

**面试官：** 单例 Service 是否线程安全？构造器注入解决了什么？

**我：**

Spring 单例只是说大家通常共用一个 Bean，不代表这个对象里的所有操作都线程安全。Service 里如果放一个不断变化的“当前订单”成员变量，不同请求就可能互相覆盖。

我会把每次请求的数据放在方法局部变量里，让依赖通过构造器注入。构造器注入方便看清依赖和写测试，不过 final 字段只能防止引用被重新赋值，不能让引用指向的对象自动线程安全。

**面试官追问 1：** 用一个成员 HashMap 保存当前用户可以吗？

**我：** 不适合。它既有并发修改问题，也容易把用户信息混在一起。即使用 ConcurrentHashMap，也还要解决身份对应关系和清理问题。请求身份应使用框架提供的请求或安全上下文。

**面试官追问 2：** 那每次请求 new 一个 Service 就安全了吗？

**我：** 也不一定，数据库和 Redis 仍然是共享的。而且自己 new 会绕过 Spring 的依赖注入和事务代理，反而可能把原来的保障弄丢。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`RequiredArgsConstructor`

原理或主题来源：[S30](sources.md#s30)、[C02](sources.md#c02)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q008"></a>
## Q008 Spring AOP 自调用会使整个业务都没有事务吗？

主题：事务；优先级：P0；状态：todo。

**面试官：** Spring AOP 自调用会使整个业务都没有事务吗？

**我：**

准确说，自调用会绕过 Spring 代理，导致被调用方法自己的事务配置没有重新生效，但不代表一定完全没有事务。

比如外层方法已经通过代理开启了事务，它在内部调用普通方法，这些代码仍然可以参与外层事务。项目批量关单就是外层 closeTimeoutOrders 开事务，再循环调用私有处理方法，所以当前是一整批共用事务，并不是每个订单各开一个。

**面试官追问 1：** 内部方法写了 REQUIRES_NEW，为什么没开启新事务？

**我：** 因为 this 调用直接到了目标对象，代理没机会拦截它。常见做法是把这部分业务移到另一个 Bean，再通过注入的代理调用，或者使用编程式事务。

**面试官追问 2：** JDK 代理和 CGLIB 有什么区别？

**我：** JDK 动态代理主要代理接口，CGLIB 通过生成子类代理类。CGLIB 不能通过重写增强 final 或 private 方法。项目实际用哪种还要结合 Spring Boot 配置，不能只看是否写了接口。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrders`

原理或主题来源：[S01](sources.md#s01)、[S04](sources.md#s04)、[C02](sources.md#c02)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q009"></a>
## Q009 createOrder 回滚会恢复 Redis、热度和下单令牌吗？

主题：事务；优先级：P0；状态：todo。

**面试官：** createOrder 回滚会恢复 Redis、热度和下单令牌吗？

**我：**

不会一起自动恢复。@Transactional 主要管理这个事务里的数据库操作，Redis 删除令牌、预扣库存、增加热度都是另外发生的。

现在项目对预扣库存加了数据库回滚后的释放回调，但已经消费的令牌不会恢复，已经增加的热度也不会跟着回滚。如果进程直接退出，回调可能根本来不及执行。所以我会分别说明数据库回滚和 Redis 补偿，不把它们说成一个跨系统事务。

**面试官追问 1：** 补偿时 Redis 又挂了怎么办？

**我：** 仅打日志不够。我会增加可持久保存的补偿任务，记录订单、资源和操作标识，恢复后有限重试并对账。补偿还必须核对占位归属，避免误释放别人后来取得的资源。

**面试官追问 2：** 热度不要求那么准，是不是就不用管了？

**我：** 可以接受小范围误差，但仍需要约定容量、保留时间和故障处理。否则长期累计错误或无效资源，也会让排行榜越来越不可信。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`registerReleaseRedisStockAfterRollback`
- [HotStatsServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/HotStatsServiceImpl.java)

原理或主题来源：[S01](sources.md#s01)、[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q010"></a>
## Q010 afterCommit、afterCompletion 与方法返回是什么关系？

主题：事务；优先级：P0；状态：todo。

**面试官：** afterCommit、afterCompletion 与方法返回是什么关系？

**我：**

我会分成三个时刻：业务方法返回、数据库提交、事务清理。方法体先返回，事务代理再提交；提交成功后才调用 afterCommit，提交或回滚完成后会调用 afterCompletion 做清理。

在本项目里，下单成功提交后尝试发 MQ，资源占位要继续保留。取消或超时关单提交后才释放 Redis 占位；下单回滚则在 afterCompletion 中补回预扣库存，资源锁也在事务完成时释放。

如果 afterCommit 抛出异常，已经提交的订单不会因此回滚。不过本项目发送 MQ 的异常在发送方法内被捕获了，不能把所有回调异常都描述为直接返回给客户端。

**面试官追问 1：** 在 afterCommit 里再写数据库可以吗？

**我：** 不能以为原事务还能帮这些新写入提交。原来的提交已经结束了，若需要可靠执行新的数据库写入，通常要通过另一个代理方法开启 REQUIRES_NEW，并单独处理失败。

**面试官追问 2：** 为什么不直接在方法 finally 里解锁？

**我：** 因为 finally 执行时，事务代理可能还没提交。锁先释放，其他请求就可能进入临界区，而前一个请求的数据库结果还没完成。放到事务完成回调里更符合这个锁要保护的范围。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`registerUnlockAfterTransaction`

原理或主题来源：[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q011"></a>
## Q011 状态机为什么不能单独保证支付和关单不会竞争？

主题：数据库并发；优先级：P0；状态：todo。

**面试官：** 状态机为什么不能单独保证支付和关单不会竞争？

**我：**

状态机能告诉我“待支付可以变成已支付”，但它只是在检查读到的状态。支付和关单两个请求可能同时读到待支付，然后都认为自己能继续。

因此更新时还要带条件，例如 UPDATE ... WHERE id=? AND status='PENDING'，并且只有更新成功的一方才能执行对应副作用。当前关单这样做了，但支付等路径还没有完全统一，所以不能说加了状态机就已经解决所有状态竞争。

**面试官追问 1：** 条件更新影响零行，应该报系统错误吗？

**我：** 通常先重新判断订单是否不存在、已完成同一操作，还是被另一种操作改变了。重复成功可以按接口约定返回已有结果，状态冲突应给明确提示，不应该一律当作系统崩溃。

**面试官追问 2：** 如果是真实支付已经扣款，但订单被关掉了呢？

**我：** 这时不能只返回“支付失败”。需要核验支付渠道的结果，进入补单、退款或人工对账流程。项目现在是模拟支付，真实渠道的这套补偿还没有实现。


**源码入口**

- [OrderStateMachine.java](../../src/main/java/com/jxbg/ticket/service/OrderStateMachine.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`payOrder`

原理或主题来源：[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q012"></a>
## Q012 不同座位都加了锁，场次库存为什么仍可能丢更新？

主题：数据库并发；优先级：P0；状态：todo。

**面试官：** 不同座位都加了锁，场次库存为什么仍可能丢更新？

**我：**

因为锁住的是不同的资源，但它们修改的是同一个场次库存。假设库存是 2，A 订一号场地、B 订二号场地，两个人都读到 2，最后都把数据库写成 1，实际上已经订出两个了。

目前代码存在这种“先读旧值，再写绝对值”的风险。改进时应让数据库执行 available_stock = available_stock - 数量，同时判断剩余库存足够，再检查影响行数。释放库存也要原子增加，并且每次取消只能回补一次。

**面试官追问 1：** MySQL 默认可重复读，不能避免吗？

**我：** 不能靠隔离级别自动改正这个写法。两个请求拿旧值计算后写相同的新值，仍可能覆盖。要把判断和扣减放到数据库的一次条件更新里。

**面试官追问 2：** 直接锁整个场次行不行？

**我：** 可以作为一种设计，但所有修改场次库存的入口都必须遵守同一协议。它会让不同资源之间也排队，并发度更低。我会优先评估数据库条件更新，再根据业务确定锁粒度。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`setAvailableStock`

原理或主题来源：[S10](sources.md#s10)、[S11](sources.md#s11)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q013"></a>
## Q013 唯一索引为什么使用 active_flag=1/NULL，而不是 1/0？

主题：数据库并发；优先级：P0；状态：todo。

**面试官：** 唯一索引为什么使用 active_flag=1/NULL，而不是 1/0？

**我：**

数据库要保留历史，但同一场次的同一资源只能有一条有效占位。项目用 session_id、seat_id、active_flag 组成唯一索引，有效时 active_flag=1，释放时改成 NULL。

MySQL 唯一索引允许这里出现多条包含 NULL 的历史记录。如果统一改成 0，第二次取消的历史记录就可能和第一次的 0 冲突。active_flag 表示业务占用，deleted 是逻辑删除，两者不能混为一谈。

**面试官追问 1：** 已经先查询没有占位，为什么还要唯一索引？

**我：** 两个并发请求都可能先查到没有。唯一索引是在真正写入时做最终限制。先查询方便给用户提示，唯一索引负责挡住竞态下的重复有效占位。

**面试官追问 2：** 换数据库，这套写法还能直接用吗？

**我：** 要重新核对那个数据库对唯一索引和 NULL 的处理，也要检查 ORM 是否真的把字段更新成 NULL，不能只把 Java 对象设为 null 就认为 SQL 一定会写入。


**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)：`uk_item_session_seat_active`

原理或主题来源：[S07](sources.md#s07)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q014"></a>
## Q014 MVCC 与行锁在预约中分别解决什么问题？

主题：数据库原理；优先级：P1；状态：todo。

**面试官：** MVCC 与行锁在预约中分别解决什么问题？

**我：**

MVCC 主要帮助普通查询读到合适的数据版本，减少读写互相阻塞。行锁和条件更新则用来协调真正的竞争修改。

例如我查询时看到一个座位可用，不代表别人接下来不能先占用它。下单还要通过唯一约束、库存条件更新等机制判断自己能不能写成功。MVCC 不能代替“这个资源现在归谁”的业务控制。

**面试官追问 1：** RC 和 RR 的快照读有什么不同？

**我：** 在 InnoDB 的普通一致性读中，RC 通常每条语句建立新快照，RR 通常复用事务中首次一致性读建立的快照。锁定读和更新不能直接按这套快照规则理解。

**面试官追问 2：** 为什么长事务会影响数据库？

**我：** 长时间保留的快照可能使旧版本不能及时清理；如果事务还持有写锁，也会挡住其他写入。因此事务里不宜夹杂长时间的外部请求和等待。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`createOrder`

原理或主题来源：[S10](sources.md#s10)、[S11](sources.md#s11)、[C03](sources.md#c03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q015"></a>
## Q015 提高隔离级别是否就能解决所有库存和状态竞争？

主题：数据库原理；优先级：P1；状态：todo。

**面试官：** 提高隔离级别是否就能解决所有库存和状态竞争？

**我：**

我不会把提高隔离级别当作万能修复。它能加强某些读写隔离，但不能替我设计正确的库存规则，也不会把 Redis 和 MySQL 变成同一个事务。

对当前项目，我会先修正库存绝对值回写和状态覆盖更新，再考虑适合的隔离级别。更强隔离还会增加等待、死锁或重试成本，要通过实际场景验证。

**面试官追问 1：** 那 SERIALIZABLE 为什么不是默认选择？

**我：** 因为更严格的并发限制通常有成本，读写容易互相等待。很多业务使用较低隔离级别，加上有针对性的锁和条件更新，就能满足正确性要求。

**面试官追问 2：** 用 version 和用 status 做条件有什么区别？

**我：** status 适合约束合法状态迁移；version 能发现更多“记录被别人改过”的情况。使用 version 必须在更新条件里检查旧版本，并成功后递增。项目目前没有完整接入版本乐观锁。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)

原理或主题来源：[S10](sources.md#s10)、[S11](sources.md#s11)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q016"></a>
## Q016 多资源预约为什么排序加锁，数据库死锁后怎么办？

主题：数据库原理；优先级：P1；状态：todo。

**面试官：** 多资源预约为什么排序加锁，数据库死锁后怎么办？

**我：**

多个资源统一排序后加锁，是为了让大家按同一个顺序拿锁，减少 A 拿着一号等二号、B 拿着二号等一号的情况。项目的资源锁实际采用不等待的 tryLock，失败会释放已经拿到的锁。

数据库仍可能因为其他行和表的更新顺序产生死锁。我会先查死锁日志，再缩短事务、统一访问顺序。确实需要重试时，要重试完整事务并限制次数，还要考虑下单令牌已经被消费等副作用。

**面试官追问 1：** Redis 锁顺序相同，数据库就绝不会死锁吗？

**我：** 不能保证。数据库还有索引记录、库存行和其他业务表的锁，保护范围比资源锁复杂。需要结合数据库实际等待关系判断。

**面试官追问 2：** 为什么持有数据库锁时不要一直重试外部接口？

**我：** 外部接口等得越久，数据库锁和连接就占得越久，其他请求也会一起堵住。重试次数、超时和事务范围应该分开设计。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`lockSeats`

原理或主题来源：[S10](sources.md#s10)、[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q017"></a>
## Q017 如何为超时订单扫描选择索引，而不是凭感觉建索引？

主题：数据库原理；优先级：P1；状态：todo。

**面试官：** 如何为超时订单扫描选择索引，而不是凭感觉建索引？

**我：**

我会先看真正的查询条件，再看执行计划。超时关单主要找 status=PENDING、expire_time 已经过期的订单，还按过期时间排序，所以可以评估以 status、expire_time 开头的联合索引。

但不是写出一个索引名字就算优化完成，还要看真实数据量、扫描行数、排序和写入成本。当前没有压测或执行计划证据的地方，我只会说是候选方案。

**面试官追问 1：** EXPLAIN ANALYZE 和 EXPLAIN 一样安全吗？

**我：** 普通 EXPLAIN 主要展示计划，EXPLAIN ANALYZE 会实际执行支持的语句并给出运行信息。对大查询要评估成本，不能在生产环境随便拿一条很重的 SQL 做分析。

**面试官追问 2：** 什么是覆盖索引和回表？

**我：** 所需列都能从选中的索引拿到，就可能用覆盖索引完成查询；如果二级索引里没有所需字段，还得根据主键去聚簇索引取整行，这一步常叫回表。


**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)：`idx_order_session_status`
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrders`

原理或主题来源：[S21](sources.md#s21)、[S07](sources.md#s07)、[C03](sources.md#c03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q018"></a>
## Q018 MyBatis-Plus 分页就能保证大表查询快吗？

主题：数据库原理；优先级：P1；状态：todo。

**面试官：** MyBatis-Plus 分页就能保证大表查询快吗？

**我：**

分页插件帮我生成分页 SQL，并不自动保证大表查得快。比如跳过几十万条再取十条，数据库仍可能做大量扫描，另外统计总数也有成本。

我会先保证筛选和排序有合适索引，再根据页面需求考虑游标分页。排序还要稳定，比如按 createTime 加 id 排序，而不是只按可能重复的创建时间。

**面试官追问 1：** 为什么游标里还要带 ID？

**我：** 多条订单可能同一时间创建，只记时间会漏数据或重复数据。用时间和 ID 组成确定顺序，下一页从上一次最后一条之后开始。数据库字段名仍要以项目实际映射为准。

**面试官追问 2：** 是不是所有列表都应该去掉总数？

**我：** 不是。后台管理可能确实需要总页数；手机端滚动列表常只需要“还有没有下一页”。我会按交互需求决定，避免每次都做没有必要的 count。


**源码入口**

- [MybatisPlusConfig.java](../../src/main/java/com/jxbg/ticket/config/MybatisPlusConfig.java)
- [PageResult.java](../../src/main/java/com/jxbg/ticket/common/PageResult.java)

原理或主题来源：[S25](sources.md#s25)、[S21](sources.md#s21)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q019"></a>
## Q019 为什么库存用 String、占位用 Set、热度用 ZSet？

主题：Redis与锁；优先级：P0；状态：todo。

**面试官：** 为什么库存用 String、占位用 Set、热度用 ZSet？

**我：**

我先看要做什么操作，再选类型。剩余库存是一个数字，用 String 做增减；已经占用哪些资源，需要判断成员和去重，用 Set；热门场馆要按分数排序，用 ZSet。

数量和资源清单不能只保留一个。库存还有一个，不代表用户选中的二号场地没被占用。另外 ZSet 的实际底层编码与 Redis 版本、数据规模有关，不能一律说成跳表。

**面试官追问 1：** ZSet 分数相同，顺序怎么定？

**我：** 同分时会按 member 的字节字典序区分；倒序查询的方向也会反过来。如果业务要求按时间或数值 ID 排同分项，就要单独设计，不能依赖字符串 ID 恰好符合数值顺序。

**面试官追问 2：** 只存一个库存数字为什么不够？

**我：** 它只能回答还剩几个，不能回答哪个可用。比如一号资源已被预约、二号资源空闲，总库存还有一，但仍必须拒绝别人再次订一号。


**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)
- [HotStatsServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/HotStatsServiceImpl.java)

原理或主题来源：[S17](sources.md#s17)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q020"></a>
## Q020 Lua 原子性指什么？脚本报错会像数据库一样回滚吗？

主题：Redis与锁；优先级：P0；状态：todo。

**面试官：** Lua 原子性指什么？脚本报错会像数据库一样回滚吗？

**我：**

Lua 的作用是把“检查资源有没有被占、库存够不够、扣库存、记占位”放到 Redis 一次脚本执行里，其他客户端命令不能插进这段操作。

但这个原子性不包含 MySQL，而且脚本运行到一半报错，也不会自动撤销前面已经执行的写入。所以我会先校验再修改，控制脚本长度，不把 Lua 说成跨系统事务。

**面试官追问 1：** Pipeline 也一次发很多命令，能代替吗？

**我：** 不能直接代替。Pipeline 主要节省网络往返，不保证这些命令不会被其他客户端命令穿插，也不方便实现依赖前一步结果的条件判断。

**面试官追问 2：** MULTI/EXEC 里某条命令运行错误，会全部回滚吗？

**我：** 不会像关系数据库那样自动全部回滚。还要区分命令入队阶段报错和执行阶段报错；执行阶段某条失败，不能认为其他已经执行的写入都被撤销了。


**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`RESERVE_SCRIPT`

原理或主题来源：[S05](sources.md#s05)、[S06](sources.md#s06)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q021"></a>
## Q021 预约脚本为什么先查资源占用再查库存？返回值有哪些？

主题：Redis与锁；优先级：P0；状态：todo。

**面试官：** 预约脚本为什么先查资源占用再查库存？返回值有哪些？

**我：**

脚本先确认库存 key 存在，再检查具体资源是不是已被占用，最后才检查总量和扣减。这样资源冲突可以给出比较明确的提示。

当前返回 1 表示预扣成功，-1 表示资源已占，-2 表示库存不足，-3 表示库存缓存未就绪。Java 再把返回值转换成业务错误。这个脚本成功只说明 Redis 预占成功，后面的数据库下单仍可能失败。

**面试官追问 1：** 三个资源只有两个能约，要不要让两个先成功？

**我：** 当前流程按一次请求整体预约处理，先全部检查，冲突就不继续扣减。如果要部分成功，需要重新定义返回结果、金额和取消规则，不能悄悄改变语义。

**面试官追问 2：** 用户把同一个 seatId 传了两遍怎么办？

**我：** 下单方法先对 seatIds 去重再排序，避免重复计算数量和金额。还需要参数层面拒绝空集合或非法 ID，不能只依赖前端。


**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`tryReserve`
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`distinct`

原理或主题来源：[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q022"></a>
## Q022 库存缓存初始化为什么可能出现半初始化？

主题：Redis与锁；优先级：P0；状态：todo。

**面试官：** 库存缓存初始化为什么可能出现半初始化？

**我：**

现在初始化是先把库存数字写进 Redis，再去查数据库明细、写已占资源集合。其他请求看到库存 key 已经存在，就可能以为初始化完成，直接进入预扣，但资源集合其实还没准备好。

所以仅仅加初始化锁、二次检查库存 key，还不能保证两份数据一起就绪。改进时需要先准备数据，再协调发布完整状态，同时处理初始化期间正在进行的预约。这个问题目前是代码分析发现的风险，不是已经修复的结论。

**面试官追问 1：** 把两个 SET 放到 Lua 里就彻底解决了吗？

**我：** 只能解决发布时不被穿插的一部分问题。从数据库读数据到发布之间，业务仍可能变化；而且库存和集合不都是简单 SET。还需要协调重建期间的写入，避免把新预占覆盖掉。

**面试官追问 2：** Redis 丢数据后，直接从数据库重建为什么有风险？

**我：** 因为有些请求可能已经在 Redis 预占，但还没提交到数据库。直接按数据库快照重建，会看不到这部分在途请求，需要暂停相关入口或设计可追踪的预占协议。


**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`ensureStockCache`

原理或主题来源：[S05](sources.md#s05)、[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q023"></a>
## Q023 释放脚本按 SREM 回补就代表任何补偿都安全了吗？

主题：Redis与锁；优先级：P0；状态：todo。

**面试官：** 释放脚本按 SREM 回补就代表任何补偿都安全了吗？

**我：**

按 SREM 真正删除的数量回补，能避免同一份占位被连续释放两次时重复加库存。但它还没有识别“这次占位属于哪个订单”。

如果旧订单的补偿很晚才到，这个资源已经被新订单占了，仅凭 seatId 删除，就可能误删新占位。因此更稳妥的设计要带预占标识或版本，释放前确认归属。当前脚本没有完整做到这一点。

**面试官追问 1：** 预扣时订单还没创建，标识从哪里来？

**我：** 可以提前生成一次预约请求的 reservationId，再把它关联到后续订单。这个标识和 seatId 一起记录，补偿时检查是不是同一次预约。这是改进方案。

**面试官追问 2：** 对账时以 Redis 还是数据库为准？

**我：** 已提交的订单和有效明细以数据库为正式记录，但还要识别未结束的预占请求。不能只做“数据库覆盖 Redis”，否则可能误处理在途预约。


**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`RELEASE_SCRIPT`

原理或主题来源：[S05](sources.md#s05)、[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q024"></a>
## Q024 Lua、Redisson、数据库唯一索引为何同时存在？

主题：Redis与锁；优先级：P0；状态：todo。

**面试官：** Lua、Redisson、数据库唯一索引为何同时存在？

**我：**

这三层管的事情不一样：Lua 先在 Redis 做快速校验和占位；Redisson 协调相同场次资源进入数据库处理的过程；数据库唯一索引最后限制不能有重复有效占位。

它们不是叠得越多就自动越安全。例如不同资源共用的场次库存、支付和关单竞争，仍然要单独处理。我会把每层保护什么讲清楚，再评估哪些机制有重复成本。

**面试官追问 1：** Redis 已经占位了，同资源的锁是不是多余？

**我：** 在预占状态可靠、所有入口都遵守协议的正常路径上，它可能有重叠作用。但发生缓存重建、恢复或其他入口操作时还要重新分析。删锁之前要先明确故障模型和数据库兜底。

**面试官追问 2：** 小项目只用数据库能不能做？

**我：** 可以，通过事务、唯一约束和正确的库存条件更新实现预约，是一个更简单的起点。Redis 预扣是否值得加入，要看实际请求压力和收益，同时接受一致性与运维成本。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`lockSeats`
- [schema.sql](../../src/main/resources/db/schema.sql)：`uk_item_session_seat_active`

原理或主题来源：[S05](sources.md#s05)、[S09](sources.md#s09)、[S07](sources.md#s07)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q025"></a>
## Q025 Redisson 看门狗怎样工作？项目所有锁都会续期吗？

主题：Redis与锁；优先级：P0；状态：todo。

**面试官：** Redisson 看门狗怎样工作？项目所有锁都会续期吗？

**我：**

看门狗可以理解为客户端在持锁期间持续给锁续期，避免业务还没执行完，锁就按一个固定时间自动过期。

但要看具体调用方式：项目的资源锁没有指定固定 leaseTime，可以使用看门狗；库存初始化和缓存重建指定了 5 秒租约，不能说它们都会自动续期。续期也依赖进程和网络正常，长暂停或断网后仍要依靠数据库约束保护关键写入。

**面试官追问 1：** 为什么不把锁过期时间设得特别长？

**我：** 太长会让进程异常退出后，别人等很久才能继续；太短又可能提前失去锁。看门狗能缓解正常运行时的时间估计问题，但不能消除所有故障。

**面试官追问 2：** 进程退出后，谁负责 unlock？

**我：** 进程已经没法主动解锁，通常靠租约到期释放。旧进程恢复或旧线程继续运行时，也不能认为自己仍然有资格写数据。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`tryLock`
- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`INIT_LOCK_LEASE_SECONDS`

原理或主题来源：[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q026"></a>
## Q026 把 Redis 换成 Cluster，预约 Lua 能原样运行吗？

主题：Redis与锁；优先级：P1；状态：todo。

**面试官：** 把 Redis 换成 Cluster，预约 Lua 能原样运行吗？

**我：**

不能直接保证。现在预约脚本同时操作库存 key 和资源集合 key，Redis Cluster 要求这种脚本涉及的 key 落在同一个槽里，而当前 key 命名没有共享的 hash tag。

可以设计成 reserve:session:{1}:stock 和 reserve:session:{1}:seats，字面上的 {1} 让同场次 key 按相同内容计算槽。但迁移还涉及旧数据、新旧版本兼容和客户端配置，项目目前没有完成 Cluster 验证。

**面试官追问 1：** 文档写着 {sessionId}，就已经能同槽了吗？

**我：** 不一定，那可能只是占位符。要看最终写入 Redis 的真实 key，花括号是否保留、括号里的内容是否相同。

**面试官追问 2：** 把所有场次都用同一个 hash tag 更省事吗？

**我：** 虽然方便跨 key 操作，但会把相关数据和请求集中到一个槽，失去分片扩展的意义。通常按真正需要一起原子操作的业务范围来分组。


**源码入口**

- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`buildKeys`

原理或主题来源：[S08](sources.md#s08)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q027"></a>
## Q027 Redis 故障切换或锁租约到期后，旧持有者还继续执行怎么办？

主题：Redis与锁；优先级：P1；状态：todo。

**面试官：** Redis 故障切换或锁租约到期后，旧持有者还继续执行怎么办？

**我：**

Redis 锁到期，不会自动让正在运行的 Java 代码停止。比如进程暂停很久，锁已经被别人拿到，旧线程恢复后仍可能继续写数据库。

所以我不会只依赖锁租约，关键写入还需要数据库唯一约束或状态、版本条件。更进一步可以设计单调递增的 fencing token，让下游拒绝旧持有者的写入，不过当前项目没有这套实现。

**面试官追问 1：** Redisson 的 RLock 就是 Redlock 吗？

**我：** 不是同一个概念。当前用的是普通 RLock，不能因为用了 Redisson 就声称采用了多节点 Redlock 协议，更不能据此保证任何故障下都绝对安全。

**面试官追问 2：** 为什么解锁不能直接 DEL 锁 key？

**我：** 旧锁可能已过期并被别人取得，直接删除会误删别人的锁。要核对持有者再释放，而且检查和删除也要原子执行；当前使用 Redisson 提供的解锁能力。


**源码入口**

- [RedissonConfig.java](../../src/main/java/com/jxbg/ticket/config/RedissonConfig.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`releaseLocks`

原理或主题来源：[S08](sources.md#s08)、[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q028"></a>
## Q028 项目使用哪种缓存读写方式，是否已经用逻辑过期？

主题：缓存；优先级：P0；状态：todo。

**面试官：** 项目使用哪种缓存读写方式，是否已经用逻辑过期？

**我：**

现在场馆详情采用旁路缓存，也就是 Cache Aside：先查 Redis，没命中再查数据库并写缓存；修改或停用场馆后删除缓存，让下次读取重新加载。

正常数据缓存 30 到 35 分钟，不存在的数据缓存空字符串 120 到 150 秒，重建时用互斥锁和二次检查。这里没有实现逻辑过期后台刷新，也没有两级缓存。新增场馆目前只保存数据，没有主动清除可能存在的空值缓存。

**面试官追问 1：** 逻辑过期和现在的做法有什么不同？

**我：** 逻辑过期通常在缓存内容里记录业务过期时间，过期后允许先返回旧值，再由后台刷新。它能减少等待，但用户可能看到短暂旧数据，适不适合要看业务。

**面试官追问 2：** 更新数据库后为什么删缓存，而不是直接更新缓存？

**我：** 删除更简单，也能避免为不再读取的数据额外计算缓存。不过删缓存也存在失败和并发旧值回填问题，不能因此就说强一致已经解决了。


**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`getVenueDetail`

原理或主题来源：[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q029"></a>
## Q029 缓存空值怎样缓解穿透，为什么 TTL 比正常值短？

主题：缓存；优先级：P0；状态：todo。

**面试官：** 缓存空值怎样缓解穿透，为什么 TTL 比正常值短？

**我：**

如果一个场馆 ID 根本不存在，每次请求都查数据库就很浪费。项目把这个“查过但不存在”的结果短暂保存成空字符串，下一次查同一个 ID 就能直接返回。

空值缓存时间比正常数据短，是为了减少后来新增数据却暂时看不到的情况。但如果每次都换一个不存在的 ID，空值缓存就帮不上太多，还要配合参数约束和限流，必要时再评估布隆过滤器。

**面试官追问 1：** Redis 返回 null 和空字符串怎么区分？

**我：** null 表示缓存没找到，要考虑回源；空字符串是项目约定的“数据库查过、结果不存在”，应直接按不存在处理。这个约定不能在序列化时混掉。

**面试官追问 2：** 新增场馆刚好命中了以前的空值缓存怎么办？

**我：** 可以在新增成功后删除对应缓存，或者等待短 TTL 到期。当前新增代码没有主动删这个空值，所以短时间查不到新数据是需要注意的窗口。


**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`CACHE_NULL_VALUE`

原理或主题来源：[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q030"></a>
## Q030 互斥重建怎样缓解击穿，目前等待策略有什么边界？

主题：缓存；优先级：P0；状态：todo。

**面试官：** 互斥重建怎样缓解击穿，目前等待策略有什么边界？

**我：**

热点场馆缓存一过期，很多请求可能一起去查数据库。互斥重建的思路就是同一时间尽量只让一个请求查库并回填，其他请求稍后再读。拿到锁后还要再查一次缓存，因为别人可能已经填好了。

当前实现拿不到锁会等 50 毫秒再递归读取，没有明确重试上限；重建锁还是固定 5 秒租约。如果数据库很慢，就可能出现持续等待或租约先过期，这部分可以改成有次数和总时长限制的等待。

**面试官追问 1：** 有互斥锁，缓存雪崩也解决了吗？

**我：** 没有。大量不同 key 同时失效，仍可能每个 key 各有一个请求打到数据库。随机 TTL、整体并发限制和降级都要结合使用，Redis 整体不可用又是另一种情况。

**面试官追问 2：** 拿不到锁时直接返回旧值行不行？

**我：** 要有旧值并且业务允许陈旧。场馆介绍可以考虑，预约库存和订单状态就要更谨慎。当前硬过期方案不一定保留旧值，不能只改一个返回语句就实现完整逻辑过期。


**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`rebuildVenueDetailCacheWithMutex`

原理或主题来源：[S09](sources.md#s09)、[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q031"></a>
## Q031 先更新数据库再删除缓存，还有哪些不一致窗口？

主题：缓存；优先级：P0；状态：todo。

**面试官：** 先更新数据库再删除缓存，还有哪些不一致窗口？

**我：**

仍然可能短暂不一致。一个情况是数据库改成功了，删缓存却失败；另一个情况是旧数据被晚到的查询重新写回缓存。

比如 A 缓存未命中并读到了旧数据库值；B 把数据库改成新值并删除缓存；随后 A 才把手里的旧值写回去。当前读侧重建锁没有和写侧形成完整协调，因此不能说先更新再删除就保证强一致。

**面试官追问 1：** 延迟双删能百分之百解决这个问题吗？

**我：** 不能。它能覆盖一部分慢请求回填，但延迟设置多长、第二次删除是否成功，都存在不确定性。需要根据一致性要求评估失效重试、版本校验或其他协议。

**面试官追问 2：** 删除缓存失败后，你会怎么处理？

**我：** 至少记录可定位的 key 和业务版本；如果要求可靠恢复，就持久记录待失效任务，后续重试和告警。单纯 catch 后打印日志，没有保证下一次一定再处理。


**源码入口**

- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)：`updateVenue`

原理或主题来源：[C01](sources.md#c01)、[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q032"></a>
## Q032 Redis 不可用时是否应该所有请求直接查数据库？

主题：缓存；优先级：P0；状态：todo。

**面试官：** Redis 不可用时是否应该所有请求直接查数据库？

**我：**

我会按用途区分。场馆介绍缓存失败，可以考虑限量回源，但不能所有请求一下子都打到数据库。下单的令牌、库存预扣和分布式锁则参与业务控制，不能因为 Redis 挂了就直接跳过。

当前代码没有完整实现统一熔断和降级方案。以后改进时，需要给不同入口规定失败行为，同时限制重试，避免一个 Redis 故障把数据库也拖垮。

**面试官追问 1：** 限流切面遇到 Redis 异常会放行，下单就能继续成功吗？

**我：** 不一定。它只表示限流这一层没有阻止请求，后面的令牌校验、库存和锁仍然依赖 Redis，照样可能失败。

**面试官追问 2：** Redis 恢复后全部清空重建，最简单吗？

**我：** 对纯缓存可能有用，但库存预占和防重令牌不是随便丢弃的。还要核对数据库正式订单与在途请求，否则可能破坏已有占用或重复提交控制。


**源码入口**

- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`isLimited`
- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)

原理或主题来源：[C01](sources.md#c01)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q033"></a>
## Q033 JWT 与 submitToken 分别解决什么，是否可以互相替代？

主题：安全与幂等；优先级：P0；状态：todo。

**面试官：** JWT 与 submitToken 分别解决什么，是否可以互相替代？

**我：**

JWT 回答“你是谁、是什么角色”，submitToken 回答“这次提交是否已经被使用”。JWT 放在请求头，可以在有效期内调用多个接口；下单令牌放在下单请求里，只能成功消费一次。

当前登录 JWT 默认两小时有效，下单令牌在 Redis 保存十分钟。它们互相不能替代，而且下单令牌目前没有绑定用户，也不保存首次返回的订单 ID。

**面试官追问 1：** JWT 里的用户信息能被别人看到吗？

**我：** 常见的签名 JWT 内容可以解码读取，签名防的是篡改，不负责保密。所以不能把密码等秘密放进 payload。

**面试官追问 2：** 能不能把下单令牌直接放进 JWT，省掉 Redis？

**我：** 仅把它签名还不能限制只能用一次。只要服务端不记录消费状态，相同有效 JWT 仍能重复提交，所以一次性使用需要额外状态或等效机制。


**源码入口**

- [JwtUtil.java](../../src/main/java/com/jxbg/ticket/security/JwtUtil.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`generateSubmitToken`

原理或主题来源：[S13](sources.md#s13)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q034"></a>
## Q034 为什么消费下单令牌不用先 GET 再 DELETE？

主题：安全与幂等；优先级：P0；状态：todo。

**面试官：** 为什么消费下单令牌不用先 GET 再 DELETE？

**我：**

因为“先看有没有，再删除”是两步，两个请求可能都先看到有，然后都继续下单。现在用一条 DEL 删除令牌，并根据是否真正删除成功决定能不能继续，同一个令牌只有一个请求能拿到成功结果。

这个方案实现的是一次性提交准入。如果令牌已经删了，但订单创建失败，当前也不会自动恢复它，更不会直接给重试请求返回上一次订单。

**面试官追问 1：** 如果还要核对令牌属于谁，怎么处理？

**我：** 可以把用户 ID 存在令牌值里，用 Lua 一次完成读取、身份比对和删除。用户身份从登录上下文获取，不相信请求体里随便传来的 ID。

**面试官追问 2：** 前端按钮点完置灰，不就不会重复了吗？

**我：** 只能减少正常用户误点，挡不住网络重试、多标签页和直接调用接口。服务端仍然要有自己的防重复规则。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`checkAndConsumeSubmitToken`

原理或主题来源：[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q035"></a>
## Q035 服务端已下单但响应丢失，用户重试应该怎么办？

主题：安全与幂等；优先级：P0；状态：todo。

**面试官：** 服务端已下单但响应丢失，用户重试应该怎么办？

**我：**

请求超时不代表订单没创建成功，可能只是响应没回来。当前实现用旧令牌重试会提示冲突，所以用户要通过自己的订单列表确认，不能马上换令牌盲目再下一单。

如果要完善重试体验，我会把用户、幂等 key、请求摘要和订单结果关联起来。相同 key、相同内容就返回原结果；内容变了就拒绝；仍在处理就明确提示。这是比一次性令牌更完整的方案，当前还没有实现。

**面试官追问 1：** 重新拿一个 token 再提交，后端会当同一次请求吗？

**我：** 当前不会，它会被当成新的提交。资源占用可能再次拦住，但那是资源冲突规则，不是完整识别了同一次业务请求。

**面试官追问 2：** 记录状态是失败后，为什么不能直接再执行？

**我：** 先要分清确定失败和结果不明。网络超时或提交结果不明时，第一次可能已经成功，应先查询持久结果，再决定是否允许重试。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`checkAndConsumeSubmitToken`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q036"></a>
## Q036 当前 submitToken 绑定用户了吗，怎样改进？

主题：安全与幂等；优先级：P0；状态：todo。

**面试官：** 当前 submitToken 绑定用户了吗，怎样改进？

**我：**

当前没有绑定。生成令牌时，Redis 的 value 只是字符串 1，不包含用户 ID。也就是说，这个机制本身只知道令牌有没有用过，不知道应该由谁使用。

改进时我会绑定登录用户和必要的请求上下文，用一次原子操作校验后消费。但如果要让重试返回原订单，还得另外保存请求结果，不能只加一个用户前缀就说完整幂等已经实现。

**面试官追问 1：** 令牌放在 URL 参数里方便吗？

**我：** 不推荐，URL 容易进入历史记录、代理日志和监控。当前下单令牌跟请求体一起发送，仍要避免完整记录到日志，并使用 HTTPS。

**面试官追问 2：** 那日志完全不记录 token，怎么排查重复请求？

**我：** 可以记录独立 requestId，或者不具备认证用途的请求摘要，配合订单 ID 关联。没必要把可直接使用的完整凭据写进日志。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`generateSubmitToken`

原理或主题来源：[S05](sources.md#s05)、[S12](sources.md#s12)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q037"></a>
## Q037 项目有角色权限，为何还需要验证订单属于当前用户？

主题：安全与幂等；优先级：P0；状态：todo。

**面试官：** 项目有角色权限，为何还需要验证订单属于当前用户？

**我：**

角色权限只能回答“普通用户能不能调用支付接口”，还不能回答“他能不能支付这个订单”。比如用户 A 把路径里的订单 ID 换成 B 的，后端仍需要核对订单归属。

当前项目有接口角色限制，但订单详情、支付、取消和部分列表查询还没有统一做好这个检查。改进应从 SecurityContext 取用户身份，在查询和更新条件里限制归属，再单独定义管理员权限。

**面试官追问 1：** 把订单 ID 改成很难猜的 UUID，能避免越权吗？

**我：** 只能降低猜测概率，不能代替授权。订单 ID 可能从日志、链接或其他接口泄露，知道 ID 也不意味着有操作权。

**面试官追问 2：** 核销员 ID 应由前端传吗？

**我：** 操作者身份应从登录上下文获取。前端可以传核销码，但不能靠它传一个 checkerId 就认定是谁操作。当前这处身份来源需要继续完善。


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

**面试官：** 禁用用户或修改角色后，旧 JWT 会立即失效吗？

**我：**

当前不一定立即失效。过滤器验证 JWT 后，主要从 token 里读取用户 ID 和角色，没有每次再查询用户是否被禁用、角色是否变化。

如果业务要求立即下线，我会增加服务端可撤销的状态，例如 tokenVersion 或撤销记录，再配合较短有效期。这样要额外查存储，是用一些无状态优势换即时控制能力。

**面试官追问 1：** 加撤销查询，是不是 JWT 就没意义了？

**我：** 不是，它仍能承载签名身份信息，跨服务验证也方便。但系统不再完全无状态，这是产品需求和可用性的取舍。

**面试官追问 2：** WebSocket 连接建立后，JWT 过期怎么办？

**我：** 只在 CONNECT 检查不会自动关闭已建立连接。要再设计过期断连、敏感消息检查或重新认证策略；当前没有完整实现这些策略。


**源码入口**

- [JwtAuthenticationFilter.java](../../src/main/java/com/jxbg/ticket/security/JwtAuthenticationFilter.java)：`doFilterInternal`

原理或主题来源：[S12](sources.md#s12)、[S13](sources.md#s13)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q039"></a>
## Q039 BCrypt、JWT 签名、HTTPS 分别保护什么？

主题：安全与幂等；优先级：P1；状态：todo。

**面试官：** BCrypt、JWT 签名、HTTPS 分别保护什么？

**我：**

它们保护不同环节。BCrypt 用来保存和校验密码，服务端不需要把密码解密回来；JWT 签名用来发现令牌有没有被篡改；HTTPS 保护客户端和服务端之间的数据传输。

所以用了 JWT 不代表密码可以明文保存，用了 BCrypt 也不代表网络传输可以不用加密。生产密钥要独立配置和轮换，不能拿公开示例密钥直接部署。

**面试官追问 1：** 同一个密码为什么两次 BCrypt 结果不同？

**我：** 因为每次通常会生成不同的随机盐，盐和成本等信息保存在编码结果里。校验时使用 matches，不是重新随机编码一次后做字符串相等比较。

**面试官追问 2：** 用了 JWT 就一定不需要防 CSRF 吗？

**我：** 不能只看 JWT。关键是浏览器会不会自动附带认证凭据。如果改成 Cookie 自动携带，就要重新评估 CSRF 防护，不能照搬当前关闭配置。


**源码入口**

- [SecurityConfig.java](../../src/main/java/com/jxbg/ticket/config/SecurityConfig.java)：`passwordEncoder`
- [AuthController.java](../../src/main/java/com/jxbg/ticket/controller/AuthController.java)：`matches`

原理或主题来源：[S12](sources.md#s12)、[S13](sources.md#s13)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q040"></a>
## Q040 为什么 Apifox 显示 HTTP 200，但业务仍然是失败？

主题：安全与幂等；优先级：P0；状态：todo。

**面试官：** 为什么 Apifox 显示 HTTP 200，但业务仍然是失败？

**我：**

这是 HTTP 状态和业务状态分开了。项目很多异常直接返回 ApiResult，里面 code 可能是 409、429 或 500，但没有同时修改 HTTP 状态，所以 Apifox 仍然显示 HTTP 200。

测试时要同时检查响应体和协议状态，压测也是一样。否则一堆业务失败被当作成功，就会得到误导性的成功率。安全链返回的 401、403 又是显式设置 HTTP 状态的，要分别看。

**面试官追问 1：** 前端用 Axios，怎么判断业务失败？

**我：** 除了处理非 2xx，还要按项目约定检查响应体 code。HTTP 200 但业务失败时，也需要提示用户，不能只依赖网络异常分支。

**面试官追问 2：** 监控只统计 HTTP 429，会有什么问题？

**我：** 会漏掉项目里 HTTP 200、业务 code=429 的限流结果。短期要补业务码指标，长期可以评估统一错误协议，但要考虑现有客户端兼容。


**源码入口**

- [GlobalExceptionHandler.java](../../src/main/java/com/jxbg/ticket/exception/GlobalExceptionHandler.java)
- [SecurityConfig.java](../../src/main/java/com/jxbg/ticket/config/SecurityConfig.java)：`writeJson`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q041"></a>
## Q041 延迟消息里面是什么，过期之后谁处理？

主题：消息与调度；优先级：P0；状态：todo。

**面试官：** 延迟消息里面是什么，过期之后谁处理？

**我：**

消息里放的是订单 ID，可以理解成“过一段时间，请再检查这笔订单”。它先进入没有业务消费者的延迟队列，到 TTL 后经死信交换机路由到关单队列，再由消费者读取。

消费者会重新查询数据库，只有订单仍待支付、确实到期，才尝试关闭和释放资源。消息自己不会检查支付状态，也不能拿创建时的状态直接决定关单。

**面试官追问 1：** 用户已经支付了，延迟消息要从队列里删掉吗？

**我：** 当前不需要主动删除。到期后消费者查到已经支付，就跳过关单。这样不用维护按订单查找并删除队列消息的复杂逻辑。

**面试官追问 2：** 消息到期了，为什么还检查 expire_time？

**我：** 因为消息延迟配置、发送时间和订单过期时间可能不完全一致，也可能收到错误或提前消息。最终业务判断应以数据库里的订单规则为准。


**源码入口**

- [OrderTimeoutRabbitConfig.java](../../src/main/java/com/jxbg/ticket/config/OrderTimeoutRabbitConfig.java)
- [OrderTimeoutMessageConsumer.java](../../src/main/java/com/jxbg/ticket/mq/OrderTimeoutMessageConsumer.java)：`closeTimeoutOrder`

原理或主题来源：[S14](sources.md#s14)、[S15](sources.md#s15)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q042"></a>
## Q042 Publisher confirm 与 consumer ACK 有什么区别？当前做到了哪一步？

主题：消息与调度；优先级：P0；状态：todo。

**面试官：** Publisher confirm 与 consumer ACK 有什么区别？当前做到了哪一步？

**我：**

我把它分成两段：publisher confirm 是生产者确认 broker 接收了发布；consumer ACK 是消费者确认这次投递已经处理到可以确认的程度。收到 confirm，不代表订单关单已经执行。

当前代码调用 convertAndSend，没有完整处理 confirm 和 returns；消费端使用 Spring 容器默认的 AUTO 确认。AUTO 通常由容器在监听方法成功返回后确认，可能受批量配置影响；它不是 RabbitMQ 的“投递后不等处理就自动确认”。当前也没有手写 manual ACK。

**面试官追问 1：** 数据库处理成功，ACK 前连接断了呢？

**我：** 未确认消息可能被重新投递，所以同一订单可能处理两遍。消费者必须能识别已经处理过，避免重复释放库存。换成手动 ACK 也不会自动消除这个窗口。

**面试官追问 2：** 消息发到交换机却没路由到队列，confirm 能发现吗？

**我：** 不能只靠 confirm。未路由消息也可能得到发布确认，通常还要开启 mandatory 并处理 return，或者设置替代交换机。当前项目还没有完整处理这条失败链路。


**源码入口**

- [RabbitOrderTimeoutMessageProducer.java](../../src/main/java/com/jxbg/ticket/mq/RabbitOrderTimeoutMessageProducer.java)
- [OrderTimeoutMessageConsumer.java](../../src/main/java/com/jxbg/ticket/mq/OrderTimeoutMessageConsumer.java)

原理或主题来源：[S16](sources.md#s16)、[S45](sources.md#s45)、[C04](sources.md#c04)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q043"></a>
## Q043 重复收到关单消息，怎样避免重复释放库存？

主题：消息与调度；优先级：P0；状态：todo。

**面试官：** 重复收到关单消息，怎样避免重复释放库存？

**我：**

当前关单先检查状态和到期时间，再尝试把 PENDING 条件更新成 CANCELLED，只有更新成功才继续释放明细和库存。因此同一订单被 MQ 或扫描任务重复触发时，不应该每次都回补一次。

Redis 释放脚本又按实际删除的占位数回补，能减少直接重复加数。不过支付竞争、不同订单共享库存和旧补偿误释放新占位仍有独立风险，不能把某个重复测试通过说成所有并发都安全。

**面试官追问 1：** MQ 和定时扫描同时处理一个订单，和重复消息有什么区别？

**我：** 触发来源不同，但都竞争同一笔订单的迁移资格。应该共用业务处理方法和条件更新，不能靠某一个入口自己记“我没处理过”。

**面试官追问 2：** 数据库已经取消，释放 Redis 前进程退出怎么办？

**我：** Redis 可能残留占位。再次扫描如果只找待支付订单，也不会自动捞到这笔已取消订单。因此还要设计库存对账或持久补偿，不能只说有定时关单就全覆盖了。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrder`
- [ReservationStockServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationStockServiceImpl.java)：`RELEASE_SCRIPT`

原理或主题来源：[S16](sources.md#s16)、[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q044"></a>
## Q044 为什么事务提交后发 MQ 还会丢事件，Outbox 改善什么？

主题：消息与调度；优先级：P0；状态：todo。

**面试官：** 为什么事务提交后发 MQ 还会丢事件，Outbox 改善什么？

**我：**

提交后发送解决了“订单还没提交，消息却先被消费”的问题，但数据库成功到消息发出之间，进程仍然可能退出。

当前发送失败会记录日志，并保留定时扫描查过期订单。更完整的方案是 Outbox：订单和待发送事件一起写进数据库，再由任务重试投递。当前没有实现 Outbox，而且即使以后用了，消费者仍然需要处理重复消息。

**面试官追问 1：** 消息发成功了，还没把 Outbox 标成成功就退出，会怎样？

**我：** 恢复后可能再发一次。所以 Outbox 主要让待发事实不容易丢，不代表只投递一次，需要事件 ID 和消费幂等配合。

**面试官追问 2：** 只开启 publisher confirm 能补上这个窗口吗？

**我：** 不能，订单提交后如果根本没来得及调用发送，就没有 confirm 可等。confirm 解决已经进入发送流程后的确认，Outbox 记录的是还必须去做的发送工作。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`sendCloseOrderMessageAfterCommit`

原理或主题来源：[S03](sources.md#s03)、[S16](sources.md#s16)、[C04](sources.md#c04)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q045"></a>
## Q045 TTL 到期为什么不保证订单刚好 15 分钟关闭？

主题：消息与调度；优先级：P1；状态：todo。

**面试官：** TTL 到期为什么不保证订单刚好 15 分钟关闭？

**我：**

TTL 到期只是消息可以被过期处理的时间，不是业务完成的精确闹钟。之后还要转发、等待消费者、查询和更新数据库，积压时会更晚。

当前延迟队列统一配置消息 TTL，和订单的 expire_time 是两个配置来源，要保持一致。消费端仍然核对到期时间，定时扫描作为补充。不能写成“到第十五分钟一定立即关单”。

**面试官追问 1：** 如果订单改成五分钟到期，队列还等十五分钟呢？

**我：** MQ 触发就偏晚了，可能先由扫描任务关闭。应该统一配置并核对旧消息、已有队列的参数兼容，不能只改一处就认为已经生效。

**面试官追问 2：** 混合不同消息 TTL，有什么需要注意？

**我：** 需要考虑消息在队列中的位置和具体队列的过期处理方式，不能简单保证短 TTL 消息一定先出来。当前采用统一队列 TTL，不是任意延迟调度平台。


**源码入口**

- [OrderTimeoutRabbitConfig.java](../../src/main/java/com/jxbg/ticket/config/OrderTimeoutRabbitConfig.java)：`orderDelayQueue`
- [application.yml](../../src/main/resources/application.yml)：`close-delay-ms`

原理或主题来源：[S14](sources.md#s14)、[S15](sources.md#s15)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q046"></a>
## Q046 应用部署两个实例，@Scheduled 会只执行一次吗？

主题：消息与调度；优先级：P1；状态：todo。

**面试官：** 应用部署两个实例，@Scheduled 会只执行一次吗？

**我：**

不会自动只运行一次。部署两个应用实例，通常每个实例都会执行自己的 @Scheduled，所以可能同时扫描到同一订单。

首先要让关单业务本身能承受重复触发，再按规模考虑调度锁、分片或调度平台。项目目前没有 XXL-JOB；批量扫描还共用一个事务，批次变大时也要考虑锁和连接占用。

**面试官追问 1：** 加一个任务锁，就不用做关单幂等了吗？

**我：** 仍然需要。任务锁可能过期，任务也可能被人工重试，MQ 同样会触发。调度协调减少重复工作，业务幂等保证重复来了也不重复生效。

**面试官追问 2：** MQ 和扫描都存在，会不会是重复设计？

**我：** 两者分工不同。MQ 按订单提供延迟触发，扫描从数据库补查漏掉的过期订单。但扫描容量和业务条件更新必须正确，才能发挥兜底作用。


**源码入口**

- [OrderTimeoutJob.java](../../src/main/java/com/jxbg/ticket/job/OrderTimeoutJob.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`closeTimeoutOrders`

原理或主题来源：[S10](sources.md#s10)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q047"></a>
## Q047 订单长期不关闭或消费积压，如何定位？

主题：消息与调度；优先级：P1；状态：todo。

**面试官：** 订单长期不关闭或消费积压，如何定位？

**我：**

我先沿消息链路查：开关是否启用，生产者有没有发送，路由是否正确，消息还在延迟队列还是已到关单队列，然后看消费者和数据库异常。

指标上会看 ready、unacked、消费速度、重复投递、数据库锁和连接等待。也要看最早过期订单已经延迟多久。当前扫描默认每分钟最多处理一个 50 条批次，积压大时可能追不上，不能只看消费者进程还活着。

**面试官追问 1：** 消息内容不是合法订单 ID，现在怎么处理？

**我：** 当前捕获数字解析错误，打印警告后返回，容器会把正常返回视为可确认。它没有进入专门的隔离队列，这和数据库暂时故障应重试是两种处理。

**面试官追问 2：** 怎样证明是 MQ 关的单，而不是定时扫描？

**我：** 需要按订单 ID 关联生产和消费日志，最好记录触发来源。当前两条路径都能关单，只看订单最终 CANCELLED，不能证明 MQ 链路已经跑通。


**源码入口**

- [OrderTimeoutMessageConsumer.java](../../src/main/java/com/jxbg/ticket/mq/OrderTimeoutMessageConsumer.java)
- [OrderTimeoutJob.java](../../src/main/java/com/jxbg/ticket/job/OrderTimeoutJob.java)

原理或主题来源：[S16](sources.md#s16)、[C04](sources.md#c04)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q048"></a>
## Q048 AOP 操作日志为何使用 REQUIRES_NEW，是否一定反映最终成功？

主题：AOP与限流；优先级：P1；状态：todo。

**面试官：** AOP 操作日志为何使用 REQUIRES_NEW，是否一定反映最终成功？

**我：**

操作失败时也希望留下审计记录，所以日志服务使用 REQUIRES_NEW，让日志有自己的事务，不跟着业务一起回滚。

但“日志里写 SUCCESS”还要看它什么时候记录。AOP 观察到方法返回，不一定已经看到外层事务最终提交，核销处尤其要核对日志切面和事务的顺序。另外核销失败记录与操作审计是不同表、不同事务位置，不能一概说失败记录都会独立保存。

**面试官追问 1：** REQUIRES_NEW 为什么会增加连接池压力？

**我：** 外层事务被挂起时可能仍占着连接，内层独立事务又要借连接。很多请求同时这样做，就可能出现所有外层都占着连接、内层等不到连接的情况。

**面试官追问 2：** 日志写失败，应不应该让订单一起失败？

**我：** 要按业务要求。当前切面会捕获日志异常并警告，尽量不影响主流程；如果是必须留证的审计场景，就需要设计可靠记录和告警，不能仅靠这个降级策略。


**源码入口**

- [OperationLogServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/OperationLogServiceImpl.java)：`REQUIRES_NEW`
- [CheckinController.java](../../src/main/java/com/jxbg/ticket/controller/CheckinController.java)：`saveFailedRecord`

原理或主题来源：[S02](sources.md#s02)、[S04](sources.md#s04)、[C02](sources.md#c02)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q049"></a>
## Q049 OperationLogContext 为什么要在 finally 中清理 ThreadLocal？

主题：AOP与限流；优先级：P1；状态：todo。

**面试官：** OperationLogContext 为什么要在 finally 中清理 ThreadLocal？

**我：**

因为 HTTP 工作线程会被复用。ThreadLocal 把数据放在当前线程上，如果这次请求结束时不清理，下一次请求可能读到上一次的业务 ID，对象也可能长期被线程引用。

所以 OperationLogAspect 在 finally 中清理 OperationLogContext，成功和异常都能覆盖。这里不是给 ThreadLocal 加锁，而是管理它的使用范围。换成异步线程时，上下文也不会自然跟过去。

**面试官追问 1：** ThreadLocal 的 key 是弱引用，为什么还可能泄漏？

**我：** 因为 value 仍可能被线程里的条目强引用。key 被回收不等于 value 立刻释放，而线程池里的线程往往活得很久，所以仍要主动 remove。

**面试官追问 2：** 异步任务需要用户身份，应该怎么传？

**我：** 在提交任务前提取必要的用户 ID、业务 ID，作为任务数据显式传递；如果确实要使用框架上下文传播工具，也要保证执行完清理，避免线程复用时串用户。


**源码入口**

- [OperationLogContext.java](../../src/main/java/com/jxbg/ticket/common/OperationLogContext.java)
- [OperationLogAspect.java](../../src/main/java/com/jxbg/ticket/aspect/OperationLogAspect.java)：`clear`

原理或主题来源：[S23](sources.md#s23)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q050"></a>
## Q050 当前限流是什么算法，INCR 与 EXPIRE 分开有什么风险？

主题：AOP与限流；优先级：P0；状态：todo。

**面试官：** 当前限流是什么算法，INCR 与 EXPIRE 分开有什么风险？

**我：**

当前做的是固定窗口计数：第一次请求开始计数并设置过期时间，在这段时间里超过次数就拦截。比如十秒内最多五次，第六次会被限制。

代码里的 INCR 和 EXPIRE 是两次调用，如果第一次加完就退出，key 可能没有过期时间。可以用 Lua 把它们合起来，但固定窗口本身还有边界突刺问题。另外当前按真实 URI 计数，不同订单 ID 会分成不同 key。

**面试官追问 1：** 跨窗口最多可能怎么突发？

**我：** 设阈值为 N，在前一个窗口末尾集中发 N 次，下一窗口开头再发 N 次，短时间内可能接近 2N。这是窗口算法特性，不是把 INCR 改成 Lua 就消失了。

**面试官追问 2：** 想限制一个用户所有支付请求，key 怎么改？

**我：** 可以使用用户 ID 加规范化路由，例如 POST /orders/{id}/pay，而不是把具体订单 ID 放进去。Spring MVC 能提供匹配到的路由模板，还要定义取不到模板时的处理。


**源码入口**

- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`isLimited`
- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`buildRateLimitKey`

原理或主题来源：[S18](sources.md#s18)、[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q051"></a>
## Q051 限流 Redis 异常时放行、直接信任 X-Forwarded-For，有何取舍？

主题：AOP与限流；优先级：P1；状态：todo。

**面试官：** 限流 Redis 异常时放行、直接信任 X-Forwarded-For，有何取舍？

**我：**

当前 Redis 计数出错时会放行，这是优先保可用性的选择，也叫 fail-open。不过敏感入口可能需要本地限流或暂时拒绝，不能所有业务都一套策略。

客户端 IP 也有边界。代码直接读 X-Forwarded-For，只有请求来自可信代理、代理正确覆盖这些头时才可信。否则调用者可以伪造 IP，绕过按 IP 的频率限制。

**面试官追问 1：** 校园网很多人共用一个出口 IP，怎么避免误伤？

**我：** 可以把账户、登录用户和 IP 的限制分层，不只按一个 IP 判断所有人。登录前还要控制针对账号的暴力尝试，阈值需要结合正常流量验证。

**面试官追问 2：** 后续下单仍依赖 Redis，限流放行还有意义吗？

**我：** 对依赖 Redis 的下单帮助有限，但有些其他业务可能仍然可用。因此限流失败策略要按接口设计，同时观察异常放行数量，不能只加 catch 就当成完成降级。


**源码入口**

- [RateLimitAspect.java](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java)：`getClientIp`

原理或主题来源：[S18](sources.md#s18)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q052"></a>
## Q052 点赞为什么使用 Redis Set 加数据库唯一索引，取消时如何避免计数漂移？

主题：点评与通知；优先级：P0；状态：todo。

**面试官：** 点赞为什么使用 Redis Set 加数据库唯一索引，取消时如何避免计数漂移？

**我：**

Redis Set 方便快速判断“这个用户有没有点赞”，数据库 review_like 保存正式关系，并用唯一约束挡住并发重复点赞。这样即使 Redis 数据丢了，数据库还有一层限制。

取消点赞不能只是查到存在就减一。两个请求可能都查到同一条点赞，所以应该用条件更新取消有效关系，只有真正更新成功的一次才扣计数。当前取消路径还没有完整按影响行数控制，存在计数漂移风险。

**面试官追问 1：** 计数 SQL 限制不小于零，不就安全了吗？

**我：** 只能防止变成负数，不能证明它等于真实点赞人数。比如真实还有十个人点赞，却因为并发取消多减了一次，九也不是负数，但仍然错了。

**面试官追问 2：** 数据库操作和 Redis SADD 之间失败怎么处理？

**我：** 要先分清数据库只是执行了 SQL，还是已经提交。当前方法在事务内执行 SADD，Redis 异常向外抛出时，数据库可能回滚；反过来 Redis 已成功、数据库后来提交失败，也可能留下缓存残留。需要明确提交时机、失败补偿和以有效点赞记录为准的对账。


**源码入口**

- [ReviewServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java)：`cancelLike`
- [ReviewMapper.java](../../src/main/java/com/jxbg/ticket/mapper/ReviewMapper.java)

原理或主题来源：[S07](sources.md#s07)、[S05](sources.md#s05)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q053"></a>
## Q053 点评分页如何避免逐条查作者和图片的 N+1 问题？

主题：点评与通知；优先级：P1；状态：todo。

**面试官：** 点评分页如何避免逐条查作者和图片的 N+1 问题？

**我：**

如果一页二十条点评，每条再单独查作者和图片，请求数据库的次数会跟着条数增加，这就是常说的 N+1 问题。

项目先查这一页，再收集点评 ID 和去重后的用户 ID，批量查图片和用户，用 Map 按 ID 组装结果。它减少了重复查询，但仍然有分页查询、可能的 count 和批量查询，不是整页只访问一次数据库。

**面试官追问 1：** 为什么作者 Map 要用用户 ID，而不是昵称？

**我：** ID 能稳定对应数据库记录，昵称可能重复或修改。批量结果也不保证按传入 ID 顺序返回，用 ID 映射才能正确关联。

**面试官追问 2：** 直接 JOIN 成一条 SQL 会不会更快？

**我：** 不一定。一对多图片 JOIN 可能把点评行放大，分页也更复杂。我会比较数据量、执行计划和返回大小，而不是只看 SQL 条数。


**源码入口**

- [ReviewServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java)：`loadReviewImages`

原理或主题来源：[S21](sources.md#s21)、[S25](sources.md#s25)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q054"></a>
## Q054 热门场馆是如何排名的？它是严格的业务统计吗？

主题：点评与通知；优先级：P1；状态：todo。

**面试官：** 热门场馆是如何排名的？它是严格的业务统计吗？

**我：**

当前用 ZSet 给场馆或资源累计热度，浏览、下单、支付、核销和点评按不同权重加分，再按分数取前几名。

它表达的是热度，不是严格订单金额报表。现在是累计榜，没有完整的按日衰减和反刷；部分加分在数据库提交前发生，后续回滚也不会自动撤销。所以我不会拿这个分数直接当精确运营统计。

**面试官追问 1：** 以后想做每日榜，怎么设计？

**我：** 可以按日期分 key，并设置保留期限；如果做最近一段时间的热度，还要考虑分桶汇总或衰减。这属于扩展设计，当前累计榜没有自动变成日榜。

**面试官追问 2：** 为什么不能让榜单一直涨、数据一直留？

**我：** 历史失效资源会长期占空间，老场馆的历史分数也可能压住新场馆。需要定义清理和排名口径，否则内存和展示效果都会受影响。


**源码入口**

- [HotStatsServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/HotStatsServiceImpl.java)：`VENUE_VIEW_SCORE`

原理或主题来源：[S17](sources.md#s17)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q055"></a>
## Q055 既然用 WebSocket，为什么还要存 user_notification？

主题：点评与通知；优先级：P0；状态：todo。

**面试官：** 既然用 WebSocket，为什么还要存 user_notification？

**我：**

WebSocket 负责让在线用户尽快看到通知，但用户可能离线、断网或刷新页面。通知表保存记录和未读状态，让用户重连后还能查询。

当前 createAndPush 是保存后马上推送，但保存可能还处于外层事务里，不等于已经提交。更稳妥的设计是提交后推送，同时允许客户端通过通知 ID 补查和去重；当前不能宣称已经保证每条通知必达。

**面试官追问 1：** 客户端收到支付成功通知，数据库随后回滚了怎么办？

**我：** 就会出现提示与事实不一致。客户端应以查询到的业务结果为准，服务端则要把推送移到合适的提交后时机，重要通知进一步考虑持久事件重试。

**面试官追问 2：** 未读数以页面内存还是数据库为准？

**我：** 数据库中的个人通知及已读状态是正式记录。页面可以即时更新显示，但重连或多个设备使用时需要重新同步，不能永远只靠本地加减。


**源码入口**

- [UserNotificationServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/UserNotificationServiceImpl.java)：`createAndPush`

原理或主题来源：[S19](sources.md#s19)、[S03](sources.md#s03)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q056"></a>
## Q056 WebSocket 在 CONNECT 验 JWT 就完成安全控制了吗？

主题：点评与通知；优先级：P0；状态：todo。

**面试官：** WebSocket 在 CONNECT 验 JWT 就完成安全控制了吗？

**我：**

CONNECT 验证只回答“连接是谁建立的”。接下来还要管他能订阅哪个目标、能往哪里发送消息，以及 token 过期或用户禁用后怎么处理。

当前项目只在 CONNECT 绑定 Principal，还没有完整的 SUBSCRIBE 和 SEND 授权。并且 simple broker 在进程内工作，多实例之间也不会自动互通，所以不能只看到 /user 路径就认为消息权限和部署问题全解决了。

**面试官追问 1：** 配置了 Origin 白名单，还需要身份认证吗？

**我：** 需要。Origin 白名单帮助限制浏览器来源，不等于确认用户身份，也不能代替具体消息目标的权限检查。

**面试官追问 2：** 用户连在实例 A，支付请求到了实例 B，怎么通知？

**我：** 当前进程内 broker 不能直接保证这条路由。可以评估外部消息代理或实例间事件路由，并处理用户连接分布；同时保留数据库补查，不能只依赖连接恰好落在同一台。


**源码入口**

- [WebSocketConfig.java](../../src/main/java/com/jxbg/ticket/config/WebSocketConfig.java)：`configureClientInboundChannel`

原理或主题来源：[S19](sources.md#s19)、[S12](sources.md#s12)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q057"></a>
## Q057 上传只检查 jpg/png 扩展名够吗，项目做了哪些检查？

主题：工程安全；优先级：P1；状态：todo。

**面试官：** 上传只检查 jpg/png 扩展名够吗，项目做了哪些检查？

**我：**

不够，文件改个后缀就能伪装。当前代码有大小限制、扩展名白名单、随机文件名和路径规范化，这些是基础检查。

如果进一步完善，我会验证文件内容和能否正常解码，限制图片尺寸与解压成本，并校验上传归属、访问权限和存储位置。项目现在是本地存储，没有接对象存储；上传文件也不会因为数据库事务回滚就自动删除。

**面试官追问 1：** 多实例部署，本地上传有什么问题？

**我：** 上传到 A 的磁盘，访问请求到 B 时可能找不到文件。需要共享存储或对象存储等方案，不能把路径字符串相同理解成文件内容共享。

**面试官追问 2：** 怎样避免用户传路径访问任意文件？

**我：** 文件名由服务端生成，解析后的目标路径必须落在指定上传目录内；返回和引用图片时还要校验属于允许的资源。不能直接把客户端输入拼成磁盘路径。


**源码入口**

- [FileUploadServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/FileUploadServiceImpl.java)：`validateImage`

原理或主题来源：[S20](sources.md#s20)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q058"></a>
## Q058 POI 导出几千行和几十万行有什么差别？

主题：工程性能；优先级：P1；状态：todo。

**面试官：** POI 导出几千行和几十万行有什么差别？

**我：**

数据量上来后，不只是 Excel 文件变大，查询结果和工作簿对象也会占内存。当前导出有数量上限，不能为了导出几十万行就简单删掉限制。

我会评估分批查询、流式工作簿、临时文件清理，必要时改成异步生成再下载。导出还必须按登录用户限制数据。当前代码把内容作为字符串写单元格，不应直接把它说成存在公式执行；以后改 CSV 或公式处理时要重新评估。

**面试官追问 1：** 用了流式工作簿，就完全不占内存了吗？

**我：** 不是，只是能把部分行数据移到临时文件，仍有窗口、样式等内存开销和磁盘开销。资源关闭、临时文件清理也要确认，不能无限并发导出。

**面试官追问 2：** 导出到一半有人新增订单，怎么保持结果一致？

**我：** 要先定义口径，例如按固定截止时间或固定 ID 范围导出；更严格时评估一致性快照，但长事务也有成本。当前不能把普通分批查询自动视为同一时刻的完整快照。


**源码入口**

- [ExcelExportServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ExcelExportServiceImpl.java)
- [ExcelExportController.java](../../src/main/java/com/jxbg/ticket/controller/ExcelExportController.java)

原理或主题来源：[S26](sources.md#s26)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q059"></a>
## Q059 怎样验证项目的并发正确性，而不只是拿到一个 QPS 数字？

主题：测试与排障；优先级：P0；状态：todo。

**面试官：** 怎样验证项目的并发正确性，而不只是拿到一个 QPS 数字？

**我：**

我会先验证业务有没有做错，再看能跑多快。至少分开测同一个资源被争抢、不同资源共享库存、支付和关单同时发生、同令牌重复提交，以及数据库或 Redis 故障后的恢复。

测试要让请求真正同时开始，并检查业务码、有效订单明细、数据库与 Redis 库存、最终状态和副作用次数。限流拒绝应单独统计。当前没有这些实测记录的地方，只能说测试方案，不能填一个猜测的并发数字。

**面试官追问 1：** 所有线程共用一个下单令牌，能测库存竞争吗？

**我：** 大部分请求会先被令牌挡住，测到的主要是重复提交控制。测不同用户争抢时，应给合法独立请求准备各自的身份和令牌。

**面试官追问 2：** 循环调用一百次接口，算并发测试吗？

**我：** 顺序调用通常不算，它们没有同时竞争同一份状态。需要并发执行和同步起跑，还要检查结果，而不是只看到一百个响应。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)
- [check-invariants.sql](../../docs/learning/labs/check-invariants.sql)

原理或主题来源：[S22](sources.md#s22)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q060"></a>
## Q060 用户说资源一直不可预约，你按什么顺序排查？

主题：测试与排障；优先级：P0；状态：todo。

**面试官：** 用户说资源一直不可预约，你按什么顺序排查？

**我：**

我先确认用户查的是哪个场次、哪个资源，再看业务响应和数据库有效占位。关联订单如果待支付，要检查是否到期；如果已支付或已核销，占用可能本来就合理。

如果订单已取消却还不可约，再查关单日志、数据库明细和 Redis 占位是否一致。资源全局启停状态和某一场次是否占用不是一回事，不能为了让 available 变成 true，就随便改资源状态或清空整个 Redis。

**面试官追问 1：** 订单 CANCELLED、Redis 仍有占位，可能是什么情况？

**我：** 可能数据库已提交取消，提交后释放 Redis 的回调失败或进程退出，也可能是其他数据漂移。要核对资源是否后来被新订单占用，不能直接按旧订单删除。

**面试官追问 2：** 修复数据之前要留下什么？

**我：** 保存订单和明细状态、相关 key、时间及日志证据，确定影响范围和预期值，再做有条件的修复并复核。这样才能解释修复了什么，而不是只知道“改完能用了”。


**源码入口**

- [SessionSeatController.java](../../src/main/java/com/jxbg/ticket/controller/SessionSeatController.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`releaseRedisStockAfterCommit`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q061"></a>
## Q061 金额为什么用 BigDecimal，equals 与 compareTo 有什么区别？

主题：Java基础；优先级：P1；状态：todo。

**面试官：** 金额为什么用 BigDecimal，equals 与 compareTo 有什么区别？

**我：**

金额用 BigDecimal 是为了按十进制规则计算，避免 double 的二进制近似值影响结算。构造时优先用字符串或明确的十进制来源，不能先算出不精确的 double 再指望它自动修好。

还有一个常见区别：2.0 和 2.00 用 compareTo 比数值相等，但 equals 还会比较小数位数。订单金额的精度、舍入和价格快照都要一起规定，光换成 BigDecimal 还不够。

**面试官追问 1：** BigDecimal.valueOf(0.1 + 0.2) 能把误差消掉吗？

**我：** 不能保证。浮点计算已经发生了，valueOf 会按这个 double 的字符串表示构造，可能保留 0.30000000000000004。应从准确的十进制输入开始计算。

**面试官追问 2：** 数据库 DECIMAL 放不下金额怎么办？

**我：** 先明确业务最大金额和小数位，做入参、计算结果和数据库列范围的一致校验。超过范围应该明确失败，不能依赖数据库自动截断或默默舍入。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`BigDecimal`
- [schema.sql](../../src/main/resources/db/schema.sql)：`DECIMAL`

原理或主题来源：[S24](sources.md#s24)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q062"></a>
## Q062 volatile、synchronized 和 Redis 锁能互相替换吗？

主题：Java基础；优先级：P1；状态：todo。

**面试官：** volatile、synchronized 和 Redis 锁能互相替换吗？

**我：**

我先看共享的数据在哪里。volatile 主要解决变量的可见性和顺序问题，不会让 count++ 变成原子操作；synchronized 和 ReentrantLock 保护当前 JVM 内的临界区；Redis 锁协调多个应用实例。

它们不能互相随意替换，数据库还要有自己的最终约束。比如两台服务器各自 synchronized，并没有锁住同一个跨进程对象。

**面试官追问 1：** volatile 保证可见，为什么 ++ 还会错？

**我：** ++ 包含读取、加一、写回。两个线程都可能读到 0，再各自写 1。看得见最新写入和整个操作不能被穿插，是两回事。

**面试官追问 2：** happens-before 就是时间上先执行吗？

**我：** 不是，它描述 Java 内存模型允许依赖的可见性和顺序关系。两个操作看起来在日志时间上有先后，不代表线程之间就建立了正确的同步。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`lockSeats`

原理或主题来源：[S09](sources.md#s09)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q063"></a>
## Q063 seatIds 去重排序、批量查询结果建 Map 有什么意义？

主题：Java基础；优先级：P1；状态：todo。

**面试官：** seatIds 去重排序、批量查询结果建 Map 有什么意义？

**我：**

下单时先去重，避免同一资源重复扣数；再排序，让多个请求按同一顺序拿资源锁。批量查询后用 ID 建 Map，是为了稳定关联对象，避免每次遍历整个列表，也不依赖数据库返回顺序。

这些都是集合在业务里的用途。要注意 Long 对象比数值别用 ==，HashMap 如果变成单例成员共享修改，也不会自动线程安全。

**面试官追问 1：** HashMap 出现 hash 冲突，会覆盖不同的 key 吗？

**我：** 不会仅因为 hash 相同就覆盖，它还要通过 equals 判断是不是同一个 key。不同 key 可以在同一个桶里，具体用链表或树等结构处理。

**面试官追问 2：** Stream.toList() 之后能 add 吗？

**我：** Java 21 的 Stream.toList() 返回不可修改列表，直接 add 会失败。需要修改时可显式收集到 ArrayList。但列表不可修改不代表里面每个对象都不可变。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`sorted`
- [ReviewServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java)：`toMap`

原理或主题来源：[S27](sources.md#s27)、[S33](sources.md#s33)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q064"></a>
## Q064 LocalDateTime、过期时间与随机订单号有什么工程边界？

主题：Java基础；优先级：P1；状态：todo。

**面试官：** LocalDateTime、过期时间与随机订单号有什么工程边界？

**我：**

LocalDateTime 表示一个不带时区的日期时间，所以多服务、数据库和部署环境需要约定一致的时间口径。显示都是晚上八点，不一定表示同一个实际时刻。

当前订单号由时间和 UUID 片段组成，并不是雪花算法。随机部分降低重复概率，数据库唯一索引才负责最终拒绝碰撞。过期时间和消息延迟也要一起核对。

**面试官追问 1：** 系统时间回拨会有什么影响？

**我：** 可能影响过期判断和按时间排序，时间拼接的编号也不能再假定严格递增。需要统一时钟并明确业务使用的是本地时间还是统一时刻。

**面试官追问 2：** 如果订单号真的碰撞，怎么办？

**我：** 唯一约束应该拒绝插入，再判断是否可以在新的事务尝试中重新生成。重试前还要处理已消费令牌和 Redis 预占，不能只重新执行 insert。当前没有完整的自动碰撞恢复流程。


**源码入口**

- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`generateOrderNo`
- [application.yml](../../src/main/resources/application.yml)：`expire-minutes`

原理或主题来源：[S07](sources.md#s07)、[S14](sources.md#s14)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q065"></a>
## Q065 别人克隆仓库如何启动，哪些配置绝不能直接公开部署？

主题：部署与运维；优先级：P0；状态：todo。

**面试官：** 别人克隆仓库如何启动，哪些配置绝不能直接公开部署？

**我：**

我会让别人先按 README 准备 JDK 21、Maven、MySQL 和 Redis，初始化数据库，用本地示例配置填写自己的连接信息和随机 JWT 密钥，再启动 Spring Boot。RabbitMQ 可以按开关选择是否启用。

仓库里保留示例和说明，真实本地配置、上传文件和日志不应直接推送。能在自己的电脑启动，也不等于已经具备可公开部署的安全和运维条件。

**面试官追问 1：** 配置文件和环境变量谁生效，怎么查？

**我：** 先看激活的 profile、启动参数和 Spring Boot 配置优先级，再核对最终绑定结果。排查时只输出必要的非敏感配置，不能把数据库密码和 JWT 密钥全部打印出来。

**面试官追问 2：** Compose 会把 Java 后端一起启动吗？

**我：** 要看仓库的 Compose 文件，不能因为有它就这样认为。当前文件用于启动 MySQL、Redis 和 RabbitMQ 等依赖，后端仍按 README 单独运行。


**源码入口**

- [README.md](../../README.md)
- [application-local.example.yml](../../src/main/resources/application-local.example.yml)
- [.gitignore](../../.gitignore)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q066"></a>
## Q066 一次业务故障需要记录哪些日志与指标？

主题：部署与运维；优先级：P1；状态：todo。

**面试官：** 一次业务故障需要记录哪些日志与指标？

**我：**

我希望能用一次请求的标识，把入口日志、订单 ID、耗时、业务码和异常堆栈串起来。如果跨 MQ，还要能把生产和消费对应上。

指标除了 HTTP 错误，还应看业务失败、数据库连接等待、过期订单处理延迟和库存不一致。项目目前没有完整链路追踪，这些是完善方向。日志里也不能为了方便排障而记录原始密码或完整 JWT。

**面试官追问 1：** 操作日志和运行日志有什么区别？

**我：** 操作日志回答谁对什么业务做了什么，方便审计；运行日志回答程序在哪一步失败、耗时在哪里，方便排障。它们可以通过业务 ID 关联，但用途和保存策略不同。

**面试官追问 2：** 用户输入直接拼进日志，有什么问题？

**我：** 可能混入敏感数据或换行等内容，影响检索和判断。应使用结构化字段、长度限制和必要脱敏，并限制日志访问权限。


**源码入口**

- [GlobalExceptionHandler.java](../../src/main/java/com/jxbg/ticket/exception/GlobalExceptionHandler.java)
- [OperationLogAspect.java](../../src/main/java/com/jxbg/ticket/aspect/OperationLogAspect.java)

原理或主题来源：[S23](sources.md#s23)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q067"></a>
## Q067 接口慢时，把线程池和数据库连接池调大就能解决吗？

主题：工程性能；优先级：P1；状态：todo。

**面试官：** 接口慢时，把线程池和数据库连接池调大就能解决吗？

**我：**

不一定，可能只是让更多请求一起等待。数据库本来就处理不过来，再放更多线程和连接进去，反而会增加内存、上下文切换和尾部延迟。

我会先查 CPU、GC、锁等待、SQL、连接池和外部依赖耗时，确定瓶颈，再调整有界队列、并发数和超时。项目使用 Java 21，但没有因此自动启用虚拟线程，也不能随便把事务写入改成 parallelStream。

**面试官追问 1：** 为什么 REQUIRES_NEW 会影响连接数估计？

**我：** 同一个请求可能同时占有外层连接，并等待内层事务需要的另一个连接。不能只按请求线程数简单一比一设置，需要结合调用结构和并发量评估。

**面试官追问 2：** 平均耗时正常，P99 却很高说明什么？

**我：** 说明大多数请求正常，但少量请求明显变慢，可能在等锁、等连接或遇到 GC。平均值把这部分稀释了，所以要结合分位数和具体慢请求分析。


**源码入口**

- [pom.xml](../../pom.xml)：`java.version`
- [OperationLogServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/OperationLogServiceImpl.java)：`REQUIRES_NEW`

原理或主题来源：[S02](sources.md#s02)、[S22](sources.md#s22)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q068"></a>
## Q068 schema.sql 加一列，线上数据库重跑就自动升级了吗？

主题：部署与运维；优先级：P1；状态：todo。

**面试官：** schema.sql 加一列，线上数据库重跑就自动升级了吗？

**我：**

不会。CREATE TABLE IF NOT EXISTS 只是在表不存在时创建，不会把已有表自动改成新的结构。把 schema.sql 改好，也不代表别人的数据库已经升级。

我会维护有版本的变更脚本，先验证兼容和回滚方案，再安排发布。Redis key 的格式、RabbitMQ 队列参数也属于需要兼容的协议。当前项目没有集成 Flyway 或 Liquibase。

**面试官追问 1：** 新旧实例同时运行，新增字段怎么发版？

**我：** 常见做法是先做向后兼容的结构变更，例如允许旧代码不传新字段；再更新应用，最后根据数据迁移情况收紧约束。不能第一步就删掉旧实例仍依赖的列。

**面试官追问 2：** 回滚 Java 包，为什么数据不一定能回滚？

**我：** 因为新版本可能已经写入新格式或执行不可逆的数据变更。代码回退不会自动恢复这些数据，所以数据库迁移要单独设计恢复方案。


**源码入口**

- [schema.sql](../../src/main/resources/db/schema.sql)
- [OrderTimeoutRabbitConfig.java](../../src/main/java/com/jxbg/ticket/config/OrderTimeoutRabbitConfig.java)

原理或主题来源：[S07](sources.md#s07)、[S14](sources.md#s14)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q069"></a>
## Q069 怎样严谨地写缓存优化与压测成果？

主题：测试与排障；优先级：P0；状态：todo。

**面试官：** 怎样严谨地写缓存优化与压测成果？

**我：**

我会记录测试用的提交、机器配置、中间件版本、数据量、缓存冷热状态、请求模型和持续时间，再报告业务成功率、吞吐和 P50、P95、P99。

缓存优化还要看数据库查询量和依赖耗时有没有真正下降。如果请求都被限流快速拒绝，响应很快也不能当作业务性能提升。目前没有自己的可复核报告，就写实现了哪些机制，不填写提升百分比和 QPS。

**面试官追问 1：** 为什么要预热？

**我：** JVM、连接池和缓存刚启动时，与稳定运行时状态不同。预热能帮助观察稳态表现，但冷启动和缓存失效场景也要另外测试，不能只保留最好看的结果。

**面试官追问 2：** 固定并发和固定到达速率有什么不同？

**我：** 固定并发下，服务变慢时用户等待更久，请求速率可能自然下降；固定到达速率仍持续发请求，更容易暴露排队和过载。报告必须说明用的是哪一种。


**源码入口**

- [lab-record.md](../../docs/learning/templates/lab-record.md)
- [VenueServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/VenueServiceImpl.java)

原理或主题来源：[S22](sources.md#s22)、[S21](sources.md#s21)。

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q070"></a>
## Q070 面试官问篮球场活动为何能看到讲座座位，怎么回答？

主题：项目设计；优先级：P1；状态：todo。

**面试官：** 面试官问篮球场活动为何能看到讲座座位，怎么回答？

**我：**

这是当前资源范围建模还不够细。查询主要按同一个场馆筛选，所以一个馆里的篮球场和讲座座位可能一起出现在结果里。

我会如实说明这个边界，后续通过场次和资源关联、或者区域约束来限定可选范围，而且查询、下单校验和库存初始化都要用同一套规则。不能只在前端隐藏几个选项，就认为后端已经限制住了。

**面试官追问 1：** 只修改查询列表有什么问题？

**我：** 用户仍可以手动把被隐藏的 seatId 传给创建订单接口。下单必须独立校验资源是否真的属于这场预约的允许范围。

**面试官追问 2：** 资源停用后，已经支付的订单怎么办？

**我：** 需要先定义业务政策，是仅禁止新预约，还是取消既有订单并通知、退款。项目不能把一个 status=0 当作已经实现完整售后处理。


**源码入口**

- [SessionSeatController.java](../../src/main/java/com/jxbg/ticket/controller/SessionSeatController.java)
- [ReservationOrderServiceImpl.java](../../src/main/java/com/jxbg/ticket/service/impl/ReservationOrderServiceImpl.java)：`getVenueId`

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q071"></a>
## Q071 如何保证学习资料后来能增删改查且不与源码脱节？

主题：学习与设计；优先级：P1；状态：todo。

**面试官：** 如何保证学习资料后来能增删改查且不与源码脱节？

**我：**

我把项目原理按主题放在 Markdown 里，题目正文、答案、追问、源码入口和个人学习状态保存在 questions.json，阅读版由工具生成。

后续可以按编号搜索和修改，不需要每次重写整份文件。代码改了以后，要重新核对相关答案和实验记录；工具能发现断链和格式错误，但不能判断一段技术解释是不是正确。

**面试官追问 1：** 直接在 interview-questions.md 改答案，为什么不推荐？

**我：** 因为下一次 export 会用 JSON 重新生成，手改内容会被覆盖。应该修改 questions.json，再生成和校验。Java 基础独立文档则可以直接编辑 Markdown。

**面试官追问 2：** 个人笔记有哪些不能放进公开仓库？

**我：** 真实密码、JWT、他人个人信息、公司内部材料和没脱敏的日志都不适合。可以保留自己的推演和脱敏实验结果，让别人也能复核。


**源码入口**

- [study.py](../../tools/study.py)：`StudyStore`
- [README.md](../../docs/learning/README.md)

**我的笔记**

待补充自己的推演、实验记录和回答。

<a id="q072"></a>
## Q072 这个项目哪些设计思想可迁移，哪些能力暂时不能宣称？

主题：学习与设计；优先级：P0；状态：todo。

**面试官：** 这个项目哪些设计思想可迁移，哪些能力暂时不能宣称？

**我：**

我觉得最有价值的是能讲清规则和失败处理：哪些数据必须一致，哪个约束负责挡重复，数据库事务管到哪里，缓存或消息失败后怎么恢复，以及身份认证和订单归属有什么不同。

当前项目可以说明这些设计和实现，但不能直接宣称生产高可用、跨系统强一致、真实支付，或者已经验证了多实例高并发。面试时我会分别说清做了什么、测过什么、还有什么待完善。

**面试官追问 1：** 只能优先改一块，你怎么选？

**我：** 我会先按影响和暴露面排优先级。涉及操作他人订单的归属权限应优先处理，库存和状态竞争也是核心正确性问题，再考虑性能和体验优化，而不是先加新技术。

**面试官追问 2：** 为什么不再多加几个中间件，让简历更丰富？

**我：** 每加一个组件都会带来新的故障和一致性问题。能解释现有方案为什么有效、哪里失效，并拿出测试证据，比仅列更多名字更有说服力。


**源码入口**

- [12-implementation-boundaries.md](../../docs/learning/12-implementation-boundaries.md)
- [OrderStateMachine.java](../../src/main/java/com/jxbg/ticket/service/OrderStateMachine.java)

**我的笔记**

待补充自己的推演、实验记录和回答。
