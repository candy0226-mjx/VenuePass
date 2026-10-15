# VenuePass 项目学习与面试手册

这是一份可以继续增删改查的学习资料。正文用 Markdown 保存，面试题以 JSON 为数据源，并生成适合直接阅读的题集。可以在 IDEA、VS Code 或 GitHub 上编辑，不需要购买笔记软件，也不需要启动数据库。

编写日期：2026-10-15。源码基线：`7661d04`。本次工作对源码进行了静态核对，并检索官方文档和公开面试资料；没有重新执行业务接口、并发压测或 RabbitMQ 联调。后文的“预期结果”均是实验验收标准，不是已经取得的测试结果。

## 从哪里开始

| 顺序 | 章节 | 学完后应能做到 |
|---|---|---|
| 1 | [结构与请求链路](01-architecture.md) | 不看代码画出请求经过的组件 |
| 2 | [业务建模与数据约束](02-business-and-data.md) | 解释 session、seat、库存和订单的关系 |
| 3 | [Spring、代理与事务](03-spring-and-transactions.md) | 区分事务回滚、Redis 补偿和事务回调 |
| 4 | [Redis、Lua 与并发控制](04-redis-and-concurrency.md) | 按实际执行顺序说明锁和预扣库存 |
| 5 | [缓存与一致性](05-cache-and-consistency.md) | 推演缓存失效、旧值回填和 Redis 故障 |
| 6 | [认证、授权与幂等](06-security-and-idempotency.md) | 区分登录令牌、下单令牌和数据权限 |
| 7 | [RabbitMQ 与定时任务](07-rabbitmq-and-scheduling.md) | 说明延迟关单及消息可靠性的边界 |
| 8 | [点评、通知、AOP 与限流](08-review-notification-and-aop.md) | 解释去重、异步副作用、日志传播和窗口算法 |
| 9 | [Java、MySQL 与性能基础](09-java-mysql-and-performance.md) | 把八股原理落到项目中的具体问题 |
| 10 | [工程实践与实验](10-engineering-and-labs.md) | 设计验收、定位故障、留下可复核证据 |
| 11 | [项目介绍与模拟追问](11-interview-playbook.md) | 连贯回答项目介绍和多轮追问 |
| 12 | [当前实现边界](12-implementation-boundaries.md) | 如实区分已实现、风险和改进设计 |
| 13 | [底层原理追问补充](13-foundation-followups.md) | 深入集合、代理、JMM、持久化、日志与资源池 |
| 随时查 | [面试题集](interview-questions.md) | 按编号查答案、追问、源码和来源 |
| 随时查 | [来源与学习借鉴](sources.md) | 查官方原理及网上面试主题的出处 |

每次学习采用四步：读章节，跳到源码，自己推演一次失败时序，脱稿回答对应题目。不能只背“使用 Redis + Lua 解决超卖”这一句话。

## 建议学习节奏

| 阶段 | 阅读与练习 | 完成条件 |
|---|---|---|
| 第 1-2 次 | 章节 1、2；L01 | 能说清 `sessionId + seatId`，知道不同场次重叠的边界 |
| 第 3-4 次 | 章节 3、4；L02、L03、L04 | 能画出事务、锁、Redis 的先后关系 |
| 第 5-6 次 | 章节 5、6；L05、L06 | 能推演旧缓存、越权、请求超时后的重试 |
| 第 7-8 次 | 章节 7、8；L07、L08、L09 | 能解释消息丢失、重复处理和通知提前发送 |
| 第 9-10 次 | 章节 9、10；L10、L11、L12 | 有自己的 SQL 分析、指标记录和排障步骤 |
| 复习 | 章节 11、12；按题库筛选 | 能在 60-90 秒内答一道题并接受两轮追问 |

这里的“次”是一段完整学习时间，不要求一天赶完几章。没有执行条件的实验先标记为“未执行”。

## 如何增删改查

正文：直接编辑对应 `.md` 文件；新增章节后在本页登记；查询可用编辑器全文搜索或 `rg "关键词" docs/learning`。

面试题：唯一数据源是 [questions.json](questions.json)，不要手工修改自动生成的 `interview-questions.md`。每道题有稳定编号、主题、优先级、参考回答、追问、源码入口、来源、学习状态和个人笔记。

在仓库根目录使用 Python 3.10+，Windows 可以把 `python` 换成 `py -3`：

当前 Windows 机器没有注册可用的 Python 启动器，但存在工具附带的 Python。可用 `& .\tools\study.ps1 list`、`& .\tools\study.ps1 search "事务"`、`& .\tools\study.ps1 check`；启动脚本会寻找已有运行时，不下载软件或修改系统设置。其他机器需自行具备 Python 3.10+，或用 `VENUEPASS_PYTHON` 指向已有解释器。

```powershell
python tools/study.py list
python tools/study.py search "事务"
python tools/study.py show Q010
python tools/study.py list --status todo --priority P0
python tools/study.py update Q010 --status learning --notes "画出了 afterCommit 的失败窗口"
python tools/study.py add --input docs/learning/templates/question.example.json
python tools/study.py delete Q073 --yes
python tools/study.py export
python tools/study.py check
```

新增示例使用 `Q073`；如果编号已存在，先换成未使用的新编号。修改题目正文可用 `update Q073 --input 文件.json`，文件只放需要替换的字段。删除命令需要显式写 `--yes`。增删改会校验数据、备份旧题库到被 Git 忽略的 `dist/learning-backups`，并重新生成题集。

也可以直接编辑 JSON，然后执行 `export` 和 `check`。这种编辑方式由 Git 保留历史，脚本不会为编辑器中已经完成的修改补做事前备份。

学习状态：`todo` 未学、`learning` 学习中、`mastered` 能脱稿讲解。优先级 `P0/P1/P2` 表示本项目的学习顺序，不是网上真实面试频率统计。个人笔记随 Git 推送，勿记录真实密码或个人敏感信息。

## 内容维护约定

1. 写清楚“当前实现”“原理解释”“改进方案”“实验记录”，不要把方案当作已完成代码。
2. 源码入口按类和方法记录；行号会变化，修改代码后重新核对方法内容。
3. 新增引用在 [sources.md](sources.md) 登记稳定来源 ID、链接、用途和核验日期。
4. 使用 [实验记录模板](templates/lab-record.md) 保存自己真正执行过的结果；不要把文档中的示例数字写成压测结论。
5. 改完执行 `python tools/study.py check`；它检查格式、编号、链接和引用，不会代替技术评审或业务测试。

## 掌握程度自检

- [ ] 能画出下单时序、数据表关系和延迟关单链路。
- [ ] 能解释三种不同的“原子性”：Redis 脚本、SQL 条件更新、数据库事务。
- [ ] 能识别“有角色权限”与“只能操作自己的订单”的区别。
- [ ] 能指出库存数据库回写、缓存初始化和支付竞争的实际边界。
- [ ] 能用日志、SQL、Redis 状态说明一次故障，而不是只说“重启一下”。
- [ ] 能说明哪些实验自己做过、哪些仍待验证。

返回 [项目首页](../../README.md)。
