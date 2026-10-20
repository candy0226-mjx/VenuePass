# 场馆通 VenuePass

VenuePass 是一个基于 Java 21 和 Spring Boot 3 的场馆预约与核销后端系统，支持分时段资源预约、订单管理、核销入场和图文点评。Maven 模块名仍为 `ticket-boot`。

项目参考 JTicket 的业务模型，使用国内 Java 后端常见技术栈重新实现，覆盖场馆资源管理、活动场次管理、预约下单、模拟支付、核销凭证、超时关单、幂等控制等核心流程。当前仓库提供后端源码、数据库初始化脚本与接口文档，不包含前端页面。

## 项目学习与面试资料

[学习手册入口](docs/learning/README.md) 按实际源码整理项目结构、业务模型、事务、Redis/Lua/锁、缓存、安全、RabbitMQ、点评通知、Java/MySQL 基础和工程实验，并提供可增删改查的面试题库、官方参考与当前实现边界。

直接阅读 [面试题集](docs/learning/interview-questions.md)，或使用 `python tools/study.py search "事务"` 搜索。修改资料后执行 `python tools/study.py check` 检查；该检查不替代业务测试。

题集已整理为 72 道主问答与 144 个完整追问的口语练习形式。基础复习可读 [Java 八股问答](docs/learning/java-interview.md)，包含 48 道与项目相关的 Java、集合、并发、JVM、Spring 和工程问题；版本边界与纠错记录见 [问答修订说明](docs/learning/interview-revision-notes.md)。

## 技术栈

| 模块 | 技术 |
|---|---|
| 基础框架 | Java 21、Spring Boot、Spring MVC |
| 数据访问 | MyBatis-Plus、MySQL |
| 缓存与并发 | Redis、StringRedisTemplate、Redisson |
| 认证授权 | Spring Security、JWT |
| 日志审计 | Spring AOP、自定义注解 |
| 接口保护 | Redis + Spring AOP 接口限流 |
| 接口文档 | Knife4j、OpenAPI、Apifox |
| 文件导出 | Apache POI |
| 消息队列 | RabbitMQ TTL + 死信队列延迟关单 |
| 实时通知 | WebSocket、STOMP |
| 构建部署 | Maven、Docker Compose |

## 功能概览

- 用户登录
- 场馆、区域、资源管理
- 活动、场次、价格规则管理
- 查询可预约资源
- 创建预约订单
- 模拟支付
- 支付流水记录
- 生成核销码
- 核销预约码
- 操作日志记录
- 工作台统计概览
- 导出我的订单
- 查询订单列表
- 图文点评、评分、点赞与管理员审核
- 场馆浏览历史、热门场馆与资源排行榜
- 支付、核销、点评审核站内通知
- 管理员、普通用户、核销员角色权限

设计对齐说明见 `docs/design-alignment.md`。
Apifox 导入文件见 `docs/apifox-openapi.yml`。

## 业务模型

系统中的预约对象统一抽象为“资源”，例如篮球场、羽毛球场、会议室、讲座座位等。

核心关系如下：

```text
场馆 venue
  -> 区域 venue_area
      -> 资源 seat

活动 activity
  -> 场次 session
      -> 可预约资源 seat
```

预约时不是只判断某个资源是否被占用，而是判断：

```text
sessionId + seatId
```

也就是说，同一个资源在不同时间段可以分别预约；同一时间段下，同一个资源只能被一个有效订单占用。

## 核心流程

```text
登录
  -> 查询场馆/活动/场次
  -> 查询指定场次下的可约资源
  -> 创建预约订单并占用资源
  -> 模拟支付并生成核销码
  -> 核销员核销预约码
  -> 生成核销记录
```

未支付订单会在超时后自动关闭，并释放已占用资源。当前保留定时扫描兜底，也已支持 RabbitMQ TTL + 死信队列实现延迟关单。

## 参数校验

系统使用 `spring-boot-starter-validation` 对接口入参做基础校验，避免非法请求进入业务逻辑。

重点校验包括：

- 登录用户名、密码不能为空
- 创建订单时 `userId`、`sessionId` 必须大于 0
- 创建订单时 `seatIds` 不能为空，且每个资源 ID 必须大于 0
- 创建订单时 `submitToken` 不能为空
- 支付流水号、核销码长度受控
- 分页参数必须大于 0，每页数量不能超过 100

校验失败时，接口会返回明确的错误信息，例如：

```json
{
  "code": 400,
  "message": "座位ID必须大于0",
  "data": null
}
```

## 订单状态机

订单状态统一由 `OrderStateMachine` 校验，避免不同业务接口各自散落判断。

当前订单状态：

```text
PENDING     待支付
PAID        已支付
CANCELLED   已取消
CHECKED_IN  已核销
```

允许的核心流转：

```text
PENDING -> PAID
PENDING -> CANCELLED
PAID    -> CHECKED_IN
```

禁止的异常流转：

```text
CANCELLED  -> PAID
CHECKED_IN -> CANCELLED
PENDING    -> CHECKED_IN
```

这样可以保证已取消订单不能支付，未支付订单不能核销，已核销订单不能再次取消。

## 数据表

| 表名 | 说明 |
|---|---|
| `users` | 用户表，包含管理员、普通用户、核销员 |
| `venue` | 场馆表 |
| `venue_area` | 场馆区域表 |
| `seat` | 可预约资源表 |
| `activity` | 活动/预约项目表 |
| `session` | 场次/预约时间段表 |
| `price_rule` | 价格规则表 |
| `reservation_order` | 预约订单表 |
| `order_item` | 订单资源明细表 |
| `payment_record` | 支付流水表 |
| `checkin_code` | 核销码表 |
| `checkin_record` | 核销记录表 |
| `operation_log` | 操作日志表 |
| `review` | 点评与审核状态 |
| `review_image` | 点评图片 |
| `review_like` | 点评点赞记录 |
| `user_notification` | 用户站内通知 |

其中 `order_item` 使用 `session_id + seat_id + active_flag` 做唯一约束，防止同一场次同一资源被重复预约。

## 本地配置

数据库建表脚本见 `src/main/resources/db/schema.sql`。
脚本内包含演示数据，默认账号密码如下：

| 用户名 | 密码 | 角色 |
|---|---|---|
| `admin` | `123456` | 管理员 |
| `user` | `123456` | 普通用户 |
| `checker` | `123456` | 核销员 |

### 不使用 Docker

当前默认启用 `local` 环境，只需要本机启动 MySQL 和 Redis。

环境要求：JDK 21、Maven 3.9+、MySQL 8+、Redis 7+。RabbitMQ 为可选依赖。

1. 克隆仓库：`git clone https://github.com/candy0226-mjx/VenuePass.git`。
2. 在 MySQL 客户端执行 `src/main/resources/db/schema.sql`，创建数据库、表和演示数据。
3. 将 `src/main/resources/application-local.example.yml` 复制为同目录的 `application-local.yml`，填写本机连接信息。个人配置已加入 `.gitignore`，不会随代码上传。
4. 在项目根目录执行 `mvn spring-boot:run`，或在 IDEA 中运行 `TicketBootApplication`。

本地配置示例：

```yaml
spring:
  datasource:
    username: root
    password: your-local-password
```

示例配置中的 JWT 密钥和初始化账号仅用于本地演示。部署时设置随机的 `JWT_SECRET`（至少 32 字节），更换演示密码，并使用独立的数据库与消息队列凭据。

构建可执行 JAR：`mvn clean package`。构建后运行 `java -jar target/ticket-boot-0.0.1-SNAPSHOT.jar`。

默认关闭 RabbitMQ 延迟关单开关，定时扫描关单仍会执行。启用与测试方式见 [RabbitMQ 延迟关单说明](docs/rabbitmq-delay-close.md)。

### 使用 Docker

如果后续使用 Docker，可以运行：

```bash
docker compose up -d
```

Compose 仅启动 MySQL、Redis 和 RabbitMQ；后端仍按上面的步骤启动。示例 MySQL 密码为 `root`，需在本地配置中对应填写。已在本机运行同端口服务时，请先调整端口，避免冲突。

Knife4j 文档地址：

```text
http://localhost:8081/api/doc.html
```

除 `/api/auth/login` 和接口文档外，业务接口默认需要在请求头携带：

```text
Authorization: Bearer <JWT>
```

## 主流程调试

1. 登录获取 JWT：`POST /api/auth/login`
2. 查询场次资源：`GET /api/sessions/1/seats`
3. 获取下单令牌：`GET /api/orders/submit-token`
4. 创建预约订单：`POST /api/orders`
5. 模拟支付并生成核销码：`POST /api/orders/{id}/pay`
6. 查询支付流水：`GET /api/payments?orderId={id}&pageNo=1&pageSize=10`
7. 查询核销码：`GET /api/orders/{id}/checkin-code`
8. 核销预约码：`POST /api/checkin/verify`
9. 查询操作日志：`GET /api/operation-logs?businessType=ORDER&businessId={id}&pageNo=1&pageSize=10`
10. 查询统计概览：`GET /api/dashboard/summary`
11. 导出我的订单：`GET /api/export/my-orders`

## Excel 导出

系统已接入 Apache POI，支持导出当前登录用户自己的订单：

```text
GET /api/export/my-orders
GET /api/export/my-orders?status=PAID
```

导出接口根据 JWT 中的用户 ID 查询数据，不接收前端传入的 `userId`，避免普通用户导出其他人的订单。单次最多导出 5000 条，后续完成角色权限后，可扩展管理员导出订单、支付流水、核销记录等后台报表。

创建订单示例：

```json
{
  "userId": 2,
  "sessionId": 1,
  "seatIds": [1],
  "submitToken": "替换为获取下单令牌接口返回的 submitToken"
}
```

创建订单成功后返回的 `data` 是订单 ID，例如：

```json
{
  "code": 200,
  "message": "success",
  "data": 5
}
```

## 并发控制

创建订单时已接入 Redisson 分布式锁：

```text
lock:reserve:session:{sessionId}:seat:{seatId}
```

实现要点：

- 使用 Redisson `RLock` 获取锁
- 多个资源按 `seatId` 排序后依次加锁
- 同一线程可重入，释放锁时校验当前线程是否持有锁
- 不手动指定锁持有时间，使用 Redisson 看门狗机制自动续期
- 事务完成后统一释放资源锁
- 数据库唯一索引 `uk_item_session_seat_active` 作为最终兜底

## 下单幂等

创建订单前需要先调用接口获取下单令牌：

```text
GET /api/orders/submit-token
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "submitToken": "2ffbe80179c04da2b7b9e5d8f7c32755",
    "expireSeconds": 600
  }
}
```

创建订单时把 `submitToken` 放到请求体中：

```json
{
  "userId": 2,
  "sessionId": 1,
  "seatIds": [1],
  "submitToken": "2ffbe80179c04da2b7b9e5d8f7c32755"
}
```

后端会在 Redis 中删除这个令牌：

```text
order:submit:token:{submitToken}
```

删除成功表示第一次提交，可以继续创建订单；删除失败表示令牌已被使用或已过期，会返回：

```json
{
  "code": 409,
  "message": "请勿重复提交或下单令牌已过期",
  "data": null
}
```

这个机制可以防止用户重复点击提交按钮导致重复下单。

## 超时关单

系统已开启定时任务，每分钟扫描一次超时未支付订单：

```text
PENDING 且 expire_time < 当前时间
```

命中的订单会被自动改为：

```text
CANCELLED
```

并释放订单明细里的资源占用，同时恢复场次库存。

测试方式：

1. 创建一个订单，不要支付
2. 在数据库中把该订单的 `expire_time` 改到当前时间之前
3. 等待 1 分钟左右，或调用 `POST /api/orders/timeout/close` 手动触发
4. 查询订单详情，状态应变为 `CANCELLED`
5. 再查可约资源，原资源应恢复为可预约

示例 SQL：

```sql
UPDATE reservation_order
SET expire_time = DATE_SUB(NOW(), INTERVAL 1 MINUTE)
WHERE id = 订单ID;
```

手动触发接口会返回本次关闭的订单数量：

```text
POST /api/orders/timeout/close?batchSize=50
```

这块可以作为面试亮点来讲：下单先占用资源，超时未支付则由后台任务关闭订单，释放 `order_item` 的资源占用，并恢复 `session.available_stock`，避免用户长期占住资源不支付。

### RabbitMQ 延迟关单

系统已补充 RabbitMQ TTL + 死信队列关单能力。开启后，创建订单事务提交成功后会发送一条延迟消息，消息内容是订单 ID：

```text
orderId
```

消息先进入延迟队列：

```text
order.delay.exchange -> order.delay.queue
```

延迟队列不设置消费者，只负责等待 TTL 到期。消息过期后会通过死信机制转发到关单队列：

```text
order.close.exchange -> order.close.queue
```

消费者监听 `order.close.queue`，收到订单 ID 后查询数据库中的最新订单状态：

```text
PENDING 且 expire_time <= 当前时间 -> 关闭订单并释放资源
PAID / CANCELLED / CHECKED_IN -> 忽略
```

本地默认关闭该功能：

```yaml
ticket:
  mq:
    order-timeout:
      enabled: false
```

如果要启用 RabbitMQ 延迟关单，先启动 RabbitMQ，再把开关改为：

```yaml
ticket:
  mq:
    order-timeout:
      enabled: true
```

延迟时间配置：

```yaml
ticket:
  order:
    expire-minutes: 15
    close-delay-ms: 900000
```

为了方便开发阶段快速验证，可以临时改成：

```yaml
ticket:
  order:
    expire-minutes: 1
    close-delay-ms: 60000
```

## 幂等控制

支付接口已支持幂等：

- `PENDING`：正常支付，写入支付流水，生成核销码
- `PAID`：重复支付不报错，直接返回已有核销码
- `CANCELLED`：拒绝支付
- `CHECKED_IN`：拒绝支付

支付流水记录在 `payment_record` 表中，一个订单只会生成一条成功支付流水，`payment_no` 也不能被不同订单重复使用。

核销接口已支持幂等：

- `VALID`：正常核销，核销码变为 `USED`
- `USED`：重复核销不重复写成功记录，返回“该预约已核销”
- `EXPIRED`：拒绝核销

测试方式：

1. 同一个订单连续调用两次 `POST /api/orders/{id}/pay`
2. 两次返回的核销码应相同
3. 同一个核销码连续调用两次 `POST /api/checkin/verify`
4. 第二次应返回“该预约已核销”

## 操作日志

系统已增加操作日志表 `operation_log`，用于记录关键业务动作：

- 创建订单：`CREATE_ORDER`
- 支付订单：`PAY_ORDER`
- 取消订单：`CANCEL_ORDER`
- 超时关单：`TIMEOUT_CLOSE_ORDER`
- 核销订单：`CHECKIN_ORDER`

查询某个订单的操作日志：

```text
GET /api/operation-logs?businessType=ORDER&businessId=订单ID&pageNo=1&pageSize=10
```

返回内容包含操作人、操作类型、业务类型、业务 ID、操作说明、操作结果和操作时间。这个功能用于后台审计和问题排查。

创建订单、支付订单、取消订单、核销订单已使用自定义注解 `@OperationLogRecord` 配合 Spring AOP 统一记录日志，业务方法不再手动调用日志服务，代码侵入性更低。超时关单属于系统后台任务，仍在任务逻辑中按每个订单单独记录日志。

## 分页查询

订单列表、支付流水、核销记录、操作日志已支持分页查询：

```text
GET /api/orders?pageNo=1&pageSize=10
GET /api/payments?pageNo=1&pageSize=10
GET /api/checkin/records?pageNo=1&pageSize=10
GET /api/operation-logs?pageNo=1&pageSize=10
```

分页响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "pageNo": 1,
    "pageSize": 10
  }
}
```

`pageNo` 默认是 1，`pageSize` 默认是 10，最大不超过 100。

## 接口限流

系统已增加 Redis + Spring AOP 接口限流能力，用于保护登录、下单、支付、核销等关键接口。

限流通过自定义注解 `@RateLimit` 开启：

```java
@RateLimit(limit = 5, windowSeconds = 10)
```

后端会按用户 ID 或 IP 生成 Redis 计数 key：

```text
rate:limit:user:{userId}:{method}:{uri}
rate:limit:ip:{ip}:{method}:{uri}
```

在固定时间窗口内超过阈值时，接口直接返回：

```json
{
  "code": 429,
  "message": "请求过于频繁，请稍后再试",
  "data": null
}
```

当前限流策略：

| 接口 | 限流规则 |
|---|---|
| 用户登录 | 1 分钟最多 5 次 |
| 获取下单令牌 | 1 分钟最多 20 次 |
| 创建订单 | 10 秒最多 5 次 |
| 模拟支付 | 1 分钟最多 10 次 |
| 核销预约码 | 1 分钟最多 20 次 |

## 业务缓存

系统已对场馆详情增加 Redis 缓存：

```text
cache:venue:detail:{venueId}
```

查询流程：

```text
查询场馆详情
  -> 先查 Redis
      -> 命中正常数据：直接返回
      -> 命中空值：直接返回 null
  -> 未命中 Redis
      -> 查询 MySQL
      -> 数据存在：写入 Redis，TTL 30 分钟
      -> 数据不存在：写入空值，TTL 2 分钟
```

缓存空值用于防止缓存穿透。例如大量请求不存在的场馆 ID 时，不会每次都打到 MySQL。

新增、修改、停用场馆时，会删除对应场馆详情缓存，保证后续查询能读取最新数据。

## 推荐测试清单

| 场景 | 预期结果 |
|---|---|
| 登录成功 | 返回 JWT |
| 不带 JWT 访问业务接口 | 返回未认证或拒绝访问 |
| 查询可约资源 | 返回资源列表和 `available` 状态 |
| 获取下单令牌 | 返回 `submitToken` 和有效期 |
| 创建订单 | 返回订单 ID，资源变为不可预约 |
| 重复使用同一下单令牌 | 返回“请勿重复提交或下单令牌已过期” |
| 重复预约同一场次同一资源 | 返回“座位已被预约” |
| 取消待支付订单 | 订单变为 `CANCELLED`，资源释放 |
| 超时关闭待支付订单 | 订单变为 `CANCELLED`，资源释放 |
| 支付订单 | 订单变为 `PAID`，生成核销码 |
| 查询支付流水 | 返回订单对应的支付记录 |
| 分页查询列表 | 返回 `records`、`total`、`pageNo`、`pageSize` |
| 高频请求限流接口 | 超过阈值后返回 `429` |
| 场馆详情缓存 | 第一次查询写入缓存，重复查询命中 Redis |
| 查询不存在场馆 | 写入短 TTL 空值，防止缓存穿透 |
| 创建订单参数错误 | 返回具体参数错误，例如“座位ID必须大于0” |
| 已取消订单再次支付 | 返回“订单已取消，不能支付” |
| 未支付订单直接核销 | 返回“订单未支付，不能核销” |
| 重复支付已支付订单 | 返回同一个核销码 |
| 核销预约码 | 订单变为 `CHECKED_IN`，核销码变为 `USED` |
| 重复核销 | 返回“该预约已核销”，不重复写成功记录 |
| 查询操作日志 | 能看到创建订单、支付、取消、超时关单、核销等记录 |
| 查询统计概览 | 返回今日订单数、今日支付金额、今日核销数、待支付订单数 |
| 导出我的订单 | 下载当前登录用户自己的订单 Excel 文件 |

## 项目亮点

- 使用 Spring Security + JWT 实现登录认证，接口默认需要携带 token。
- 使用 Redisson 分布式锁控制预约并发，锁粒度精确到 `sessionId + seatId`。
- 使用 Redis 下单令牌实现接口幂等，防止用户重复提交订单。
- 使用 Redis 缓存场馆详情，并通过空值缓存防止缓存穿透。
- 使用数据库唯一约束兜底，防止极端情况下重复占用资源。
- 创建订单、写入订单明细、扣减场次库存放在同一事务中，保证数据一致性。
- 支持未支付订单超时关闭，自动释放资源并恢复库存。
- 支持 RabbitMQ TTL + 死信队列延迟关单，定时扫描作为兜底方案。
- 使用 Redis + Spring AOP 实现接口限流，保护登录、下单、支付、核销等关键接口。
- 支付接口支持幂等，重复支付不会重复生成核销码，也不会重复写支付流水。
- 核销接口支持幂等，重复核销不会重复写成功记录。
- 使用 Jakarta Validation 做接口参数校验，非法参数在进入业务逻辑前直接拦截。
- 使用订单状态机集中控制状态流转，防止已取消订单支付、未支付订单核销等非法操作。
- 使用 Spring AOP + 自定义注解统一记录订单关键状态流转日志，支持后台审计和问题排查。
- 使用 MyBatis-Plus 分页插件实现订单、支付流水、核销记录、操作日志分页查询。
- 提供工作台统计接口，支持后台首页展示今日订单、支付金额、核销数、待支付订单等核心指标。
- 使用 Apache POI 实现 Excel 导出，导出接口基于当前登录用户隔离数据。
- 通过统一响应和统一异常处理，让接口返回结构更稳定。

## 面试讲法

可以这样概括项目：

```text
这是一个场馆预约与核销系统，主要解决体育场馆、活动场次、座位资源的在线预约问题。

我负责后端核心业务设计与实现，包括场馆资源模型、场次预约、订单创建、模拟支付、核销码生成、核销记录，以及超时未支付订单自动关闭。

项目中重点处理了资源重复预约问题。创建订单时先使用 Redisson 按 sessionId + seatId 加锁，利用 RLock 的可重入、安全释放和看门狗自动续期能力提升锁可靠性，再通过数据库唯一索引做兜底，保证同一时间段下同一资源不会被重复占用。

同时我还做了下单、支付和核销的幂等处理。下单前先获取 Redis 令牌，提交订单时原子删除令牌，避免用户重复点击生成多笔订单；支付成功后会单独写入支付流水，重复支付返回已有核销码且不重复写流水；重复核销不会重复写成功记录。未支付订单超时后会自动关闭并释放资源，避免用户长期占用资源不支付。系统还通过 Redis + AOP 对登录、下单、支付、核销等关键接口做限流保护，防止高频刷接口；对场馆详情这类读多写少数据使用 Redis 缓存，并用短 TTL 空值缓存防止缓存穿透。系统还记录订单创建、支付、取消、超时关闭、核销等操作日志，方便后台审计和排查订单状态变化原因。针对订单、流水和日志这类增长较快的数据，使用 MyBatis-Plus 分页查询，避免一次性加载全部记录。
```

## 后续计划

- 完成 RabbitMQ 真实环境的端到端联调，验证消息过期、重复消费与库存释放。
- 补充单元测试和接口自动化测试用例。
