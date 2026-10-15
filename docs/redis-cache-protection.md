# Redis 缓存保护

## 当前实现

场馆详情接口 `/api/venues/{id}` 使用 Redis 缓存热点数据，当前包含三类保护：

- 缓存穿透：数据库查不到的场馆 ID，会短时间缓存空值，避免不存在的数据反复打到数据库。
- 缓存击穿：缓存失效后，使用 Redisson 分布式锁控制同一场馆只有一个线程回源查询数据库并重建缓存。
- 缓存雪崩：正常数据和空值缓存都增加随机过期时间，避免大量 key 在同一时间集中失效。

## 关键 Key

```text
cache:venue:detail:{venueId}
lock:cache:venue:detail:{venueId}
rank:venue:hot
history:venue:{userId}
```

## 读流程

1. 先查 Redis `cache:venue:detail:{venueId}`。
2. 命中正常 JSON，直接返回场馆详情。
3. 命中空值，说明数据库不存在，直接返回空结果。
4. 未命中时抢 Redisson 锁。
5. 抢到锁后再次检查缓存，防止重复重建。
6. 仍未命中才查询数据库。
7. 查询到数据，写入 Redis，并设置基础 TTL + 随机 TTL。
8. 查询不到数据，写入短 TTL 空值。

## 写流程

管理员新增场馆后不主动写缓存。

管理员修改或禁用场馆后删除对应详情缓存：

```text
cache:venue:detail:{venueId}
```

下一次查询时再重新加载数据库最新数据，这就是 Cache Aside 模式。
