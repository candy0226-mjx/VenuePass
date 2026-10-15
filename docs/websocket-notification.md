# WebSocket 站内通知

## 功能说明

系统在关键业务节点自动生成站内通知，并通过 WebSocket 实时推送给在线用户。

- 用户支付成功后，通知用户订单已支付。
- 核销员核销成功后，通知用户订单已完成核销，可以发布点评。
- 用户提交点评后，通知管理员有新的点评待审核。
- 管理员审核点评后，通知用户审核通过或未通过。

通知会先写入 `user_notification` 表，再尝试实时推送。即使用户不在线，消息也不会丢失，后续可以通过通知列表接口查询。

## REST 接口

基础路径为 `/api`。

- `GET /notifications`：查询当前用户通知列表，支持 `readFlag`、`pageNo`、`pageSize`。
- `GET /notifications/unread-count`：查询当前用户未读通知数。
- `PUT /notifications/{id}/read`：标记单条通知已读。
- `PUT /notifications/read-all`：全部标记已读。

这些接口都需要登录后携带 JWT：

```http
Authorization: Bearer {token}
```

## WebSocket 接入

连接地址：

```text
ws://localhost:8081/api/ws
```

STOMP CONNECT 帧需要携带 JWT：

```text
Authorization: Bearer {token}
```

订阅当前用户通知：

```text
/user/queue/notifications
```

订阅当前用户未读数变化：

```text
/user/queue/notification-count
```

前端以后可以在用户登录成功后建立 WebSocket 连接，收到通知后刷新右上角消息数量或弹出提示。
