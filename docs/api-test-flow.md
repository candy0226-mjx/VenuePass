# 接口测试流程

本文档用于在 Apifox 中从零跑通 Ticket Boot 的核心接口流程。

## 1. 环境准备

启动本机依赖：

```text
MySQL
Redis
```

启动后端项目：

```text
http://localhost:8081/api
```

Knife4j 地址：

```text
http://localhost:8081/api/doc.html
```

Apifox 环境前置 URL：

```text
http://localhost:8081/api
```

## 2. 登录获取 Token

接口：

```http
POST /auth/login
```

请求体：

```json
{
  "username": "user",
  "password": "123456"
}
```

预期结果：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 2,
    "username": "user",
    "nickname": "普通用户",
    "role": "USER",
    "token": "JWT_TOKEN"
  }
}
```

后续业务接口需要在请求头携带：

```text
Authorization: Bearer JWT_TOKEN
```

Apifox 中可以把登录接口返回的 `data.token` 保存到环境变量 `bearerToken`，然后在项目鉴权里使用：

```text
{{bearerToken}}
```

### 2.1 角色权限说明

系统当前有三类账号：

```text
admin / 123456     管理员
checker / 123456   核销员
user / 123456      普通用户
```

权限边界：

```text
USER
- 查询场馆、活动、场次、可约资源
- 创建订单、取消订单、模拟支付
- 查询自己的订单、核销码
- 上传点评图片、发布点评、查看我的点评、点赞点评
- 导出自己的订单

CHECKER
- 查询核销记录
- 核销预约码

ADMIN
- 新增、修改、停用场馆
- 新增区域、资源、活动、场次、价格
- 查询支付流水、操作日志、工作台统计
- 查询待审核点评、通过点评、驳回点评
- 手动关闭超时订单
```

未登录访问业务接口会返回：

```json
{
  "code": 401,
  "message": "请先登录",
  "data": null
}
```

角色不匹配会返回：

```json
{
  "code": 403,
  "message": "无访问权限",
  "data": null
}
```

## 3. 基础数据检查

### 3.1 场馆列表

```http
GET /venues
```

预期：返回场馆列表。

### 3.1.1 场馆详情

```http
GET /venues/{venueId}
```

预期：

- 返回场馆详情
- 如果场馆状态为启用，会写入 Redis 热门场馆排行
- 如果当前已登录，会写入当前用户的浏览记录

### 3.1.2 热门场馆

```http
GET /venues/hot?limit=10
```

预期：

- 返回按浏览次数倒序排列的场馆
- 多次访问同一个启用场馆详情后，该场馆 `viewCount` 会增加

### 3.1.3 浏览记录

```http
GET /venues/history?limit=20
```

预期：

- 返回当前登录用户最近浏览过的场馆
- 同一个场馆多次浏览只保留一条记录，并更新最近浏览时间

### 3.2 区域列表

```http
GET /venues/1/areas
```

预期：返回场馆下的区域，例如篮球区、讲座区。

### 3.3 资源列表

```http
GET /venues/1/seats
```

预期：返回场馆下的资源，例如篮球场、座位。

### 3.4 活动列表

```http
GET /activities
```

预期：返回活动/预约项目列表。

### 3.5 场次列表

```http
GET /activities/1/sessions
```

预期：返回活动下的场次，也就是可预约时间段。

### 3.6 价格列表

```http
GET /price-rules
```

预期：返回价格规则。

## 4. 主业务流程

### 4.1 查询可约资源

接口：

```http
GET /sessions/1/seats
```

说明：

`sessionId` 表示一个场次/时间段，返回这个时间段下所有资源的可预约状态。

预期结果：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sessionId": 1,
      "seatId": 1,
      "seatNo": "COURT-01",
      "name": "1号篮球场",
      "price": 80.00,
      "available": true
    }
  ]
}
```

选择一个 `available: true` 的 `seatId` 用于创建订单。

### 4.2 获取下单令牌

接口：

```http
GET /orders/submit-token
```

预期结果：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "submitToken": "xxxxxxxxxxxxxxxx",
    "expireSeconds": 600
  }
}
```

说明：

`submitToken` 只能使用一次，用于防止重复提交订单。

### 4.3 创建订单

接口：

```http
POST /orders
```

请求体：

```json
{
  "userId": 2,
  "sessionId": 1,
  "seatIds": [1],
  "submitToken": "替换为上一步返回的 submitToken"
}
```

预期结果：

```json
{
  "code": 200,
  "message": "success",
  "data": 订单ID
}
```

说明：

创建成功后，`sessionId + seatId` 对应资源会被占用。

### 4.4 查询订单详情

接口：

```http
GET /orders/{orderId}
```

预期：

- 订单状态为 `PENDING`
- 订单明细中能看到预约资源

### 4.5 模拟支付

接口：

```http
POST /orders/{orderId}/pay
```

请求体：

```json
{
  "paymentNo": "TEST_PAY_001"
}
```

预期：

- 订单状态变为 `PAID`
- 返回核销码
- 写入支付流水

### 4.6 查询支付流水

接口：

```http
GET /payments?orderId={orderId}&pageNo=1&pageSize=10
```

预期：

- 返回该订单对应的支付流水
- `status` 为 `SUCCESS`

### 4.7 查询核销码

接口：

```http
GET /orders/{orderId}/checkin-code
```

预期：

- 返回核销码
- `status` 为 `VALID`

### 4.8 核销预约码

接口：

```http
POST /checkin/verify
```

请求体：

```json
{
  "code": "替换为支付或查询核销码接口返回的 code",
  "checkerId": 3
}
```

预期：

- 返回 `success`
- 订单状态变为 `CHECKED_IN`
- 核销码状态变为 `USED`
- 写入核销记录

### 4.9 查询核销记录

接口：

```http
GET /checkin/records?orderId={orderId}&pageNo=1&pageSize=10
```

预期：

- 返回核销记录
- `result` 为 `SUCCESS`

### 4.10 上传点评图片

接口：

```http
POST /uploads/review-images
```

请求类型：

```text
multipart/form-data
```

参数：

```text
file: 选择图片文件
```

预期：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileName": "4b1362cddcac427492c60a50be03d58d.png",
    "url": "/review-images/2026/08/29/4b1362cddcac427492c60a50be03d58d.png",
    "size": 68
  }
}
```

说明：

- 上传接口需要登录
- 支持 `jpg、jpeg、png、gif、webp`
- 单张图片最大 5MB
- 返回的 `url` 放到发布点评的 `imageUrls` 里
- 完整访问地址是：`http://localhost:8081/api` + `url`

### 4.11 发布点评

接口：

```http
POST /reviews
```

请求体：

```json
{
  "orderId": 订单ID,
  "rating": 5,
  "content": "场馆体验不错，下次还会预约。",
  "imageUrls": [
    "/review-images/2026/08/29/4b1362cddcac427492c60a50be03d58d.png"
  ]
}
```

预期：

- 只有状态为 `CHECKED_IN` 的订单可以点评
- 一个订单只能发布一次点评
- 返回点评 ID
- 写入点评图片
- 写入 `CREATE_REVIEW` 操作日志
- 新发布的点评状态为 `待审核`，不会立刻出现在场馆点评列表中

### 4.12 查询场馆点评

接口：

```http
GET /venues/1/reviews?pageNo=1&pageSize=10
```

预期：

- 返回该场馆下的点评列表
- 能看到评分、内容、图片、点赞数和用户昵称
- 只展示管理员审核通过的点评

### 4.13 查询我的点评

接口：

```http
GET /reviews/my?pageNo=1&pageSize=10
```

预期：

- 只返回当前登录用户发布的点评
- 已删除的点评不会出现在列表中
- 能看到 `待审核`、`已通过`、`已驳回` 等状态

### 4.14 查询待审核点评

接口：

```http
GET /reviews/pending?pageNo=1&pageSize=10
```

说明：

该接口用于管理员处理用户提交的点评。当前阶段已经在接口里校验 `ADMIN` 角色。

预期：

- 使用 `admin / 123456` 登录后可以查询
- 使用普通用户登录会返回 `403 只有管理员可以审核点评`

### 4.15 通过点评

接口：

```http
POST /reviews/{reviewId}/approve
```

预期：

- 待审核点评状态变为 `已通过`
- 通过后可以在 `GET /venues/{venueId}/reviews` 中展示
- 重复通过会返回 `409 只有待审核点评可以通过`

### 4.16 驳回点评

接口：

```http
POST /reviews/{reviewId}/reject
```

预期：

- 待审核点评状态变为 `已驳回`
- 驳回后不会在场馆点评列表展示
- 重复驳回会返回 `409 只有待审核点评可以驳回`

### 4.17 点赞点评

接口：

```http
POST /reviews/{reviewId}/like
```

预期：

- 第一次点赞成功
- 点评 `likeCount` 加 1
- Redis 写入 `review:like:{reviewId}:users`
- 重复点赞返回 `409 已点赞，请勿重复操作`

### 4.18 取消点赞

接口：

```http
DELETE /reviews/{reviewId}/like
```

预期：

- 取消点赞成功
- 点评 `likeCount` 减 1
- Redis 移除当前用户点赞标记
- 重复取消返回 `409 还未点赞，不能取消`

### 4.19 删除点评

接口：

```http
DELETE /reviews/{reviewId}
```

预期：

- 只能删除自己发布的点评
- 删除后 `GET /reviews/{reviewId}` 返回 `404 点评不存在`
- 删除后 `GET /reviews/my` 和 `GET /venues/{venueId}/reviews` 不再展示该点评
- 删除点评会清理对应 Redis 点赞集合

### 4.20 查询操作日志

接口：

```http
GET /operation-logs?businessType=ORDER&businessId={orderId}&pageNo=1&pageSize=10
```

预期：

能看到订单创建、支付、核销等操作日志。

点评相关日志可以查：

```http
GET /operation-logs?businessType=REVIEW&businessId={reviewId}&pageNo=1&pageSize=10
```

### 4.21 查询工作台统计

接口：

```http
GET /dashboard/summary
```

预期：

返回今日订单数、今日支付金额、今日核销数、待支付订单数等统计数据。

### 4.22 导出我的订单

接口：

```http
GET /export/my-orders
```

也可以按状态筛选：

```http
GET /export/my-orders?status=PAID
```

预期：

下载 `我的订单.xlsx` 文件，只包含当前登录用户自己的订单。

## 5. 异常流程测试

### 5.1 重复使用下单令牌

操作：

1. 获取一次 `submitToken`
2. 第一次创建订单成功
3. 第二次继续使用同一个 `submitToken` 创建订单

预期：

```json
{
  "code": 409,
  "message": "请勿重复提交或下单令牌已过期",
  "data": null
}
```

### 5.2 重复预约同一资源

操作：

1. 创建订单预约 `sessionId = 1`、`seatIds = [1]`
2. 再次创建订单预约同一个 `sessionId + seatId`

预期：

```json
{
  "code": 409,
  "message": "座位已被预约",
  "data": null
}
```

### 5.3 已取消订单不能支付

操作：

1. 创建订单
2. 调用取消订单接口
3. 再调用模拟支付接口

预期：

```json
{
  "code": 409,
  "message": "订单已取消，不能支付",
  "data": null
}
```

### 5.4 未支付订单不能核销

操作：

未支付订单一般没有核销码。可以通过已存在核销码或异常数据测试订单状态机。

预期：

```json
{
  "code": 409,
  "message": "订单未支付，不能核销",
  "data": null
}
```

### 5.5 参数校验失败

创建订单时传入非法资源 ID：

```json
{
  "userId": 2,
  "sessionId": 1,
  "seatIds": [0],
  "submitToken": "有效 submitToken"
}
```

预期：

```json
{
  "code": 400,
  "message": "座位ID必须大于0",
  "data": null
}
```

### 5.6 接口限流

操作：

短时间内连续请求登录接口超过 5 次。

预期：

```json
{
  "code": 429,
  "message": "登录尝试过于频繁，请稍后再试",
  "data": null
}
```

## 6. 扩展功能测试

### 6.1 Redis 场馆详情缓存

操作：

1. 删除 Redis key：`cache:venue:detail:1`
2. 请求 `GET /venues/1`
3. 查看 Redis 是否生成 `cache:venue:detail:1`

预期：

第一次查询后写入场馆详情缓存。

### 6.2 Redis 空值缓存

操作：

1. 请求不存在的场馆：`GET /venues/999999`
2. 查看 Redis 是否生成 `cache:venue:detail:999999`

预期：

Redis 写入短 TTL 空值缓存，用于防止缓存穿透。

### 6.3 缓存一致性

操作：

1. 请求 `GET /venues/{id}` 写入缓存
2. 修改场馆：`PUT /venues/{id}`
3. 查看 Redis 中对应缓存是否被删除
4. 再次请求详情，重新写入新缓存

预期：

修改或停用场馆后，对应场馆详情缓存会被删除。

### 6.4 超时关单

操作：

1. 创建一个待支付订单
2. 将该订单的 `expire_time` 改到当前时间之前
3. 调用：

```http
POST /orders/timeout/close?batchSize=50
```

预期：

- 订单状态变为 `CANCELLED`
- 资源释放
- 再查可约资源时 `available` 变为 `true`

## 7. 推荐演示顺序

面试或项目演示时，推荐按这个顺序讲：

```text
业务模型
  -> sessionId + seatId 防重复预约
  -> 下单令牌防重复提交
  -> Redisson 分布式锁控制并发
  -> 支付和核销幂等
  -> 超时关单释放资源
  -> Redis 缓存和限流
  -> Redis ZSet 浏览记录和热门场馆排行
  -> AOP 操作日志
  -> 点评发布和 Redis 点赞去重
  -> 工作台统计和 Excel 导出
```
