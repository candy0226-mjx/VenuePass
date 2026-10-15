# 08 点评、通知、AOP 与限流

## 核销到点评：业务前置条件

[ReviewServiceImpl](../../src/main/java/com/jxbg/ticket/service/impl/ReviewServiceImpl.java) 校验当前用户是订单所有者，并要求订单 `CHECKED_IN` 才能发布。评分为整数 0-5，初始状态 2 待审核；状态 1 且有效的点评才对普通查询可见。

点评、图片分表避免一行塞入不定数量列；图片记录 sort 保持顺序。当前存图片 URL，本地文件系统存二进制。管理员审批通过/驳回采用条件更新，但还需要检查影响行数再发通知，否则并发审批可能产生相互矛盾的通知。

## 点赞为什么不能只看 Redis

当前流程：Redis Set 快速判断 -> 写 `review_like` -> 数据库原子 SQL 更新 like_count -> SADD 用户。MySQL 的 `(review_id,user_id,active_flag)` 唯一约束是最终去重，Redis 丢失后 DB 仍能阻止有效重复点赞。

取消点赞需要先找到有效点赞记录。当前没有完整的并发 CAS 和每步影响行数判定，两个取消请求可能读到同一记录并触发多次计数修改。即使递减 SQL 限制不小于 0，也不等于计数与事实一定一致。

可校验：`review.like_count` 是否等于有效 `review_like` 数；Redis Set 是否和有效用户集合一致。修复方向是让“取消有效记录成功一次”成为计数递减的资格，缓存失败重试，对账时以数据库事实为准。

## N+1 查询是怎么避开的

点评分页先查当前页，收集点评 ID 与用户 ID，批量加载图片和用户，构建 Map 后组装 VO。这样减少逐条遍历时的数据库请求，但不代表只执行一条 SQL；分页 count、主列表和批量关联查询仍各有成本。

图片列表是否可信、是否属于当前用户上传、是否含恶意内容，还需独立校验，不会因为点评表有校验就自动安全。

## AOP 日志为何适合横切逻辑

切面识别 `@OperationLogRecord`，在业务返回或抛错后记录操作人、业务 ID、动作、结果。SpEL 可从参数/结果提取 ID；核销使用 `OperationLogContext` 的 ThreadLocal 补充业务 ID，并在 finally 清理。

ThreadLocal 是线程上的局部存储，不是每个 HTTP 用户永久一个容器。线程池复用会让未清理的数据泄漏到下一任务；异步线程也不会自动继承普通 ThreadLocal。异常路径更要 clear。[S23](sources.md)

切面先后决定限流失败会不会产生操作日志、日志是否看到最终提交结果。`RateLimitAspect` 明确 `@Order(0)`；其他切面/事务层次需要实际检查，不能靠注解在文件里的上下排列推断。

审计日志与普通运行日志的目标不同：前者记录业务行为可查询，后者提供堆栈和排障上下文。不要把完整请求体、密码和 JWT 直接写入任何日志。

## 当前限流是固定窗口计数

[RateLimitAspect](../../src/main/java/com/jxbg/ticket/aspect/RateLimitAspect.java) 按登录用户或 IP + HTTP 方法 + 实际 URI 构造 key；INCR，首次请求 EXPIRE，超过阈值拒绝。它不是滑动窗口、漏桶或令牌桶。

两个关键边界：INCR 与首次 EXPIRE 分开发出，进程在中间失败会留下无 TTL 计数；`/orders/1/pay` 与 `/orders/2/pay` 的实际 URI 不同，计数维度被订单 ID 拆开，未必符合“用户每分钟所有支付总共 10 次”的业务期望。[S18](sources.md)

当前 Redis 异常时限流放行，称 fail-open；安全敏感入口可选择更保守策略，但要权衡可用性。代码直接信任 X-Forwarded-For，还需由可信代理覆盖该头，否则调用者可能伪造 IP。

改进例子是 Lua 原子 INCR+EXPIRE、归一化路由模板、有限窗口、按用户与资源多维限流。这里是分析，不代表已有代码改成这些算法。

## WebSocket：数据库事实和实时提示

[WebSocketConfig](../../src/main/java/com/jxbg/ticket/config/WebSocketConfig.java) 注册 `/api/ws`，STOMP CONNECT 帧解析 JWT 并绑定 Principal；简单 broker 使用 `/queue`、`/topic`，个人订阅使用 `/user/queue/notifications`。

[UserNotificationServiceImpl](../../src/main/java/com/jxbg/ticket/service/impl/UserNotificationServiceImpl.java) 先保存通知再尝试推送，离线时可从通知列表补查。数据库未读状态才是持久记录，WebSocket 只是实时加速；重连后应补查并按通知 ID 去重。[S19](sources.md)

但目前调用点可能仍处于业务事务内，save 返回不等于外层事务已提交；推送却立刻发生，可能产生“客户端看见成功，事务随后回滚”。更好的安排是事务提交后发送、失败补查或 Outbox。

CONNECT 身份绑定不等于每条 SUBSCRIBE/SEND 都已授权。当前没有完整的消息级目标校验；simple broker 也是进程内的，多实例不自动共享订阅。长连接过期令牌、断线心跳、背压和部署排空都属于工程问题。

## 上传与导出

[FileUploadServiceImpl](../../src/main/java/com/jxbg/ticket/service/impl/FileUploadServiceImpl.java) 限制 5MB、扩展名，随机命名并规范化路径。扩展名白名单不足以证明是真图片；可研究魔数检测、解码重编码、像素限制、鉴权与对象存储。当前尚未实现 OSS/MinIO，不要写进已用技术栈。[S20](sources.md)

[ExcelExportServiceImpl](../../src/main/java/com/jxbg/ticket/service/impl/ExcelExportServiceImpl.java) 当前限制导出规模，使用 POI。更大数据应研究分页读取、流式工作簿、临时文件清理与异步导出。当前 `setCellValue(String)` 写字符串，不是调用 `setCellFormula`；未来改成 CSV 或自动识别公式的导出时，再评估以 `= + - @` 开头的用户内容风险，不能直接认定现有 XLSX 已存在公式注入。[S26](sources.md)

对应 Q048-Q058；实验 L08、L09。返回 [目录](README.md)。
