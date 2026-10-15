# 来源与学习借鉴

核验日期：2026-10-15。网上面试资料仅用于收集主题与追问方向，回答按本项目代码重新组织；技术行为以官方文档和指定版本源码为依据。没有逐字搬运整篇文章，也不把公开题目当某公司真实面试频率统计。

项目依赖以 [pom.xml](../../pom.xml) 为准。部分官方参考链接默认展示最新版本，本手册只借用适用于项目的通用机制；Spring 7、Redis 8 等新增 API 不代表本项目已支持。源码入口比外部文章中的假设优先。

## 官方原理资料

<a id="s01"></a>
### S01 Spring 声明式事务

[Using @Transactional](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)。核对代理调用、默认回滚与注解配置；不使用页面中仅新版本支持的全局新 API。

<a id="s02"></a>
### S02 Spring 事务传播

[Transaction Propagation](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html)。用于区分 REQUIRED、REQUIRES_NEW 和连接池影响。

<a id="s03"></a>
### S03 Spring 6.1.14 事务同步回调

[TransactionSynchronization Javadoc](https://docs.spring.io/spring-framework/docs/6.1.14/javadoc-api/org/springframework/transaction/support/TransactionSynchronization.html)。用于确认 afterCommit/afterCompletion 时机、回调异常和数据库资源语义。

<a id="s04"></a>
### S04 Spring AOP 代理

[Proxying Mechanisms](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html)。用于解释 JDK/CGLIB、自调用和可代理范围；不将 Spring 7 的新注解用于项目。

<a id="s05"></a>
### S05 Redis Lua

[Scripting with Lua](https://redis.io/docs/latest/develop/programmability/eval-intro/)。用于确认脚本执行的隔离性、时长和 key 参数要求。

<a id="s06"></a>
### S06 Redis 事务

[Transactions](https://redis.io/docs/latest/develop/using-commands/transactions/)。对比 MULTI/EXEC、WATCH 与错误不自动回滚；不借用 Redis 8.4 新命令描述当前 Redis 7 代码。

<a id="s07"></a>
### S07 MySQL 唯一索引

[MySQL 8.4 CREATE INDEX](https://dev.mysql.com/doc/refman/8.4/en/create-index.html)。核对 UNIQUE、NULL 与联合索引语义。

<a id="s08"></a>
### S08 Redis Cluster

[Redis cluster specification](https://redis.io/docs/latest/operate/oss_and_stack/reference/cluster-spec/)。用于多 key 同槽、hash tag 和故障切换边界。

<a id="s09"></a>
### S09 Redisson RLock

[Locks and synchronizers](https://redisson.pro/docs/data-and-services/locks-and-synchronizers/)。用于重入、leaseTime、watchdog 和持有线程语义；本项目版本为 3.32.0，具体调用以源码为准。

<a id="s10"></a>
### S10 MySQL 锁定读

[MySQL 8.4 Locking Reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html)。用于讨论 SELECT FOR UPDATE、计数更新与并发协调。

<a id="s11"></a>
### S11 MySQL MVCC

[InnoDB Multi-Versioning](https://dev.mysql.com/doc/refman/8.4/en/innodb-multi-versioning.html)。用于解释一致性读与版本，而不是把 MVCC 当业务锁。

<a id="s12"></a>
### S12 Spring Security 过滤器架构

[Servlet Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html)。用于理解认证过滤器、安全链与上下文，未将新版本默认行为无条件套用。

<a id="s13"></a>
### S13 JWT 标准

[RFC 7519](https://www.rfc-editor.org/rfc/rfc7519)。用于 token 声明、签名与加密区别，结合项目 JJWT 验签代码阅读。

<a id="s14"></a>
### S14 RabbitMQ 3.13 TTL

[Time-To-Live and Expiration](https://www.rabbitmq.com/docs/3.13/ttl)。核对消息 TTL、队列 TTL 与实际过期处理。

<a id="s15"></a>
### S15 RabbitMQ 3.13 死信交换机

[Dead Letter Exchanges](https://www.rabbitmq.com/docs/3.13/dlx)。用于 DLX 路由和死信转发的边界。

<a id="s16"></a>
### S16 RabbitMQ 3.13 确认机制

[Consumer Acknowledgements and Publisher Confirms](https://www.rabbitmq.com/docs/3.13/confirms)。用于生产端确认、消费端确认及重投递语义。

<a id="s17"></a>
### S17 Redis ZSet

[Redis sorted sets](https://redis.io/docs/latest/develop/data-types/sorted-sets/)。用于成员去重、计分与排序 API；底层编码需结合运行版本与数据规模。

<a id="s18"></a>
### S18 Redis 计数限流

[INCR](https://redis.io/docs/latest/commands/incr/)。用于计数器、TTL 与多命令间隙问题。

<a id="s19"></a>
### S19 Spring STOMP 用户目标

[User Destinations](https://docs.spring.io/spring-framework/reference/web/websocket/stomp/user-destination.html)。用于用户目标路由与多服务器需要额外协调的机制。

<a id="s20"></a>
### S20 文件上传安全

[OWASP File Upload Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html)。用于扩展名、内容验证、大小、文件名与存储策略的核验。

<a id="s21"></a>
### S21 MySQL 执行计划

[EXPLAIN](https://dev.mysql.com/doc/refman/8.4/en/explain.html)。用于查询计划和执行分析，不将示例估算值当成项目指标。

<a id="s22"></a>
### S22 负载测试实践

[Apache JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)。用于负载测试运行方式、资源使用和结果收集。

<a id="s23"></a>
### S23 Java 21 ThreadLocal

[ThreadLocal API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html)。用于线程局部数据的寿命与清理。

<a id="s24"></a>
### S24 Java 21 BigDecimal

[BigDecimal API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html)。用于构造方式、不可变性、scale 与比较。

<a id="s25"></a>
### S25 MyBatis-Plus 分页

[Pagination Plugin](https://baomidou.com/en/plugins/pagination/)。对照项目 3.5.7 配置理解分页插件，注意官网关于 3.5.9+ 的依赖拆分不适用于本项目版本。

<a id="s26"></a>
### S26 Apache POI 电子表格处理

[Spreadsheet How-To](https://poi.apache.org/components/spreadsheet/how-to.html)。用于比较普通/流式读写、大数据量和临时文件成本。

<a id="s27"></a>
### S27 Java 21 HashMap

[HashMap API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html)。用于哈希查找、无序、扩容成本和非同步语义。

<a id="s28"></a>
### S28 Redis 持久化

[Redis persistence](https://redis.io/docs/latest/operate/oss_and_stack/management/persistence/)。用于区分 RDB/AOF、刷盘策略和数据损失窗口。

<a id="s29"></a>
### S29 Redis 复制

[Redis replication](https://redis.io/docs/latest/operate/oss_and_stack/management/replication/)。用于异步复制、确认与故障切换边界。

<a id="s30"></a>
### S30 Spring Boot 3.3 自动配置

[Auto-configuration](https://docs.spring.io/spring-boot/3.3/reference/using/auto-configuration.html)。用于自动配置条件、已有 Bean 与默认配置的关系。

## 网上面试主题采集

以下为公开资料的主题入口，并非直接复制其答案。阅读时回到上述官方资料和项目方法核对，尤其警惕“强一致”“绝不丢消息”“加锁就不会超卖”等省略前提的结论。

<a id="c01"></a>
### C01 JavaGuide 缓存面试主题

[缓存基础常见面试题](https://www.javaguide.cn/database/redis/cache-basics.html)。借鉴主题：穿透/击穿/雪崩、读写顺序、缓存失效失败；项目问题见 Q028-Q032。

<a id="c02"></a>
### C02 JavaGuide Spring 面试主题

[Spring 与 Spring Boot 面试题](https://interview.javaguide.cn/system-design/spring.html)。借鉴主题：IoC、代理、自调用和事务；项目问题见 Q007-Q010、Q048。

<a id="c03"></a>
### C03 JavaGuide MySQL 面试主题

[MySQL 面试题](https://interview.javaguide.cn/database/mysql.html)。借鉴主题：索引、事务、MVCC、锁；项目问题见 Q012-Q018。

<a id="c04"></a>
### C04 JavaGuide 消息队列面试主题

[消息队列面试题](https://www.javaguide.cn/high-performance/message-queue/message-queue-interview-questions.html)。借鉴主题：可靠性、幂等、积压与选型；项目问题见 Q041-Q047。

## 如何继续增加资料

新条目使用未占用的 S/C 编号，保留明确 HTML 锚点，填写标题、直接链接、用于解决哪个问题和核验日期。官方资料优先，社区题目用于发现盲区。链接失效时标记并替换，不伪造访问记录。

返回 [目录](README.md)。
