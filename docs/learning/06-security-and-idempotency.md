# 06 认证、授权与幂等

## 两种 token 不是一回事

| 项目 | 登录 JWT | 下单 submitToken |
|---|---|---|
| 用途 | 证明请求者的登录身份和角色声明 | 限制一次提交动作 |
| 当前生成 | 签名 JWT，默认 7200 秒 | UUID 字符串，Redis 保存 600 秒 |
| 提交位置 | `Authorization: Bearer ...` | JSON 的 `submitToken` 字段 |
| 当前校验 | 验签、过期校验，设置 SecurityContext | 对 Redis key 执行一次 DEL |
| 当前是否返回旧订单 | 不适用 | 不会，重复使用返回业务冲突 |

JWT payload 通常可解码读取，签名保障完整性，不是把所有声明加密。不能放密码，也不能只解码不验签。[JwtUtil](../../src/main/java/com/jxbg/ticket/security/JwtUtil.java)、[S13](sources.md)。

## 认证和路由角色规则

[JwtAuthenticationFilter](../../src/main/java/com/jxbg/ticket/security/JwtAuthenticationFilter.java) 解析 JWT，把 `LoginUser` 放进认证上下文；[SecurityConfig](../../src/main/java/com/jxbg/ticket/config/SecurityConfig.java) 再判断 USER、ADMIN、CHECKER。

登录和文档路径放行；普通用户创建、支付和取消订单；核销员及管理员核销；管理员维护场馆、查看日志和支付流水。规则顺序很重要，具体路径要放在会覆盖它的宽泛路径之前。[S12](sources.md)

## 最容易被追问的数据权限

“有 USER 角色才能查订单”并不表示“只能查自己的订单”。源码中订单详情、取消、支付、核销码查询主要按订单 ID 操作，尚未统一校验 JWT 用户与 `order.userId` 一致；列表也允许外部传 `userId`，未形成普通用户强制归属过滤。

创建订单读取请求体 `userId`，核销读取请求体 `checkerId`，同样不能视为可信身份。即使测试账号只有一个普通用户，这个风险也不会自动消失。相比之下，点评创建与个人通知在相关方法中明确检查用户归属，可以作为对照学习。

改进方式：身份取自受信任的 SecurityContext；按角色决定是否允许管理员跨用户查询；业务写入 SQL 带上归属条件；覆盖两个不同 USER 账号的互查与互改测试。改变前端按钮或隐藏参数不等于后端授权。

## 下单令牌的准确语义

Redis 单条 `DEL` 的返回结果具有原子消费效果，不需要先 GET 再 DEL。并发使用同一个 token 只有一个调用能删除成功。但当前 value 为 `1`，没有绑定用户、请求摘要或订单结果。

于是存在这些边界：

- 第一次提交后数据库失败，token 也已消耗，需要重新获取。
- 服务端已创建订单但响应丢失，使用旧 token 只能得到冲突，不能靠它查询原订单。
- 两个不同 token 提交相同业务，仍需资源占用和数据库约束阻止重复预约。
- 知道他人 token 的请求未被令牌机制本身阻止，身份绑定需要另做。

它适合称为“一次性防重复提交令牌”。若要可靠重试返回同一结果，可研究 `(userId, idempotencyKey)` 唯一记录、请求摘要、PROCESSING/SUCCESS/FAILED 状态与结果保存；相同 key 不同请求体必须拒绝。这是改进方案，不是当前已实现流程。

## 顺序幂等和并发幂等不同

当前顺序重复支付 PAID 订单可返回已有核销码；顺序重复核销 USED 凭证可返回已核销。两个并发请求都先读到旧状态时，不能只靠这段 if 判断确保副作用只发生一次。

超时关闭有 `WHERE status=PENDING` 的条件更新；支付、取消和核销未全部采用同样的竞争控制。只让关单 CAS，而支付仍无条件按 ID 覆盖，也可能出现“关单成功释放资源后又被改为 PAID”。所有竞争状态变更需要共同遵守原子条件写入与影响行数判定。

## JWT 的生命周期与密码

当前 JWT 直接携带角色，过滤器不逐次重新读取禁用状态。因此禁用用户或修改角色后，旧令牌在过期前可能仍有效。按需求可以增加 tokenVersion、撤销表或短 access token + refresh token，但都尚未实现。

BCrypt 使用带盐的自适应哈希，不能解密回原密码；校验用 encoder.matches，不重新生成哈希后直接比较字符串。密钥与账号从环境/本地配置管理，不写入学习笔记或仓库。

项目关闭 CSRF，使用显式 Bearer 请求头而非默认浏览器会话。是否需要 CSRF 防护取决于凭证传输方式，换成 Cookie 自动携带后需重新评估；CORS 也不代替鉴权。WebSocket 还要单独检查订阅权限。

## HTTP 状态码与业务 code

全局异常处理大多返回 `ApiResult`，不显式设置 HTTP 状态，因此 HTTP 200 的 JSON 中仍可能 `code=500`、`409` 或 `429`。Security 的 401/403 则显式设置响应状态。自动测试、监控和压测必须同时检查协议状态与业务字段。

不能只看 Apifox 的绿色“通过”或只统计 HTTP 200 就认为业务成功。对应 Q033-Q040、Q056、Q065；实验 L06。返回 [目录](README.md)。
