# RabbitMQ 延迟关单说明

## 功能目标

用户创建预约订单后，系统发送一条订单超时检查消息。消息先进入延迟队列，等待订单支付过期时间后，再通过死信机制转发到关单队列。消费者拿到订单 ID 后重新查询订单状态，如果订单仍然是 `PENDING` 并且已经过期，就关闭订单并释放预约资源。

## 实现方式

项目采用 RabbitMQ 的 `TTL + 死信队列` 实现延迟关单：

- `order.delay.exchange`：订单延迟交换机。
- `order.delay.queue`：订单延迟队列，设置 `x-message-ttl`。
- `order.close.exchange`：订单关单交换机。
- `order.close.queue`：订单关单队列，消费者真正处理超时订单。
- 延迟队列过期后，通过 DLX 转发到关单队列。

## 执行流程

```text
用户创建订单
 -> 订单状态为 PENDING
 -> 事务提交后发送订单 ID 到 order.delay.queue
 -> 消息在延迟队列等待 ticket.order.close-delay-ms
 -> TTL 到期后消息进入 order.close.queue
 -> 消费者根据订单 ID 查询订单
 -> 如果订单仍是 PENDING 且已过期：关闭订单、释放座位、记录系统操作日志
 -> 如果订单已支付、已核销、已取消：忽略该消息
```

## 关键代码

- 队列、交换机、死信绑定：`OrderTimeoutRabbitConfig`
- 发送延迟消息：`RabbitOrderTimeoutMessageProducer`
- 消费关单消息：`OrderTimeoutMessageConsumer`
- 订单关闭逻辑：`ReservationOrderServiceImpl#closeTimeoutOrder`
- RabbitMQ 未启用时的空实现：`NoopOrderTimeoutMessageProducer`

## 配置开关

默认本地环境未启用 RabbitMQ，避免没有 RabbitMQ 服务时影响项目启动：

```yaml
ticket:
  mq:
    order-timeout:
      enabled: false
```

安装并启动 RabbitMQ 后，改成：

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
    close-delay-ms: 900000
```

`900000` 毫秒等于 15 分钟。

## 兜底机制

项目仍保留定时任务扫描超时订单：

```yaml
ticket:
  order:
    timeout-scan-cron: "0 * * * * ?"
    timeout-batch-size: 50
```

这样 RabbitMQ 延迟消息负责及时关单，定时任务负责兜底补偿，避免消息丢失或 RabbitMQ 短暂不可用导致订单长期占用资源。

## 简历写法

可以写：

> 使用 RabbitMQ TTL + 死信队列实现预约订单延迟关单，订单创建成功后在事务提交后发送延迟消息，消费者到期后校验订单状态并关闭未支付订单，同时释放预约资源；保留定时任务作为补偿机制，提高订单状态一致性和系统可靠性。
