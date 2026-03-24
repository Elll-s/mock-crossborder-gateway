
# 🚀 跨境支付核心 Webhook 接收网关 (Mock Gateway)

> 本项目是一个基于 Spring Boot 3 构建的分布式跨境支付回调接收网关 MVP。针对高并发支付网络中的“重试风暴”与“极端宕机”场景，实现了金融级的防重校验与数据强一致性保障。

## 🛠️ 核心技术栈
- **后端架构:** Java 17, Spring Boot 3, Spring Web
- **数据持久化:** Spring Data JPA, Hibernate
- **数据库:** MySQL 8.0 (Docker 容器化部署)
- **API 规范:** OpenAPI 3.0 (Swagger UI)
- **运维部署:** AWS EC2, Linux Daemon (nohup), Fat JAR 构建

## 🔥 核心高价值亮点 (Core Features)

### 1. 绝对幂等性防御 (Idempotency)
针对海外网关（如 YooMoney/Qiwi）因网络波动导致的回调重试风暴，基于 Spring Data JPA 衍生查询机制 (`existsByOrderId`) 构建防重校验层。利用底层 SQL 的 `LIMIT 1` 极速熔断机制，确保接口调用的绝对幂等，彻底杜绝资金重复入账的 P0 级风险。

### 2. 金融级 ACID 事务控制 (Transaction & Crash Recovery)
针对业务执行中断导致的“薛定谔的账单”痛点，采用 `@Transactional` 注解开启 InnoDB 引擎的事务隔离结界。严格依赖数据库底层的 **Undo Log 崩溃恢复机制**，确保订单数据落盘的绝对原子性（Atomicity），在任何运行时异常（RuntimeException）或物理断电下实现数据安全回滚。

### 3. AOP 全局异常接管与防御网
利用 `@RestControllerAdvice` 结合面向切面编程（AOP），实现对系统运行期异常的全局拦截。将底层的异常堆栈转化为标准化的 JSON 错误报文（如 `INVALID_AMOUNT`），有效防止 Tomcat 500 报错的堆栈泄露，提升网关的鲁棒性与安全性。

## 🖥️ 交互式 API 沙盒 (OpenAPI)
项目已全量集成 `springdoc-openapi`，提供工业级 API 交互界面。
*(上传到 GitHub 后，请在这里贴一张你在浏览器里测试 Swagger 网页的截图！)*

## ⚙️ 生产环境极速部署指令
```bash
# 1. 构建 Fat JAR
mvn clean package -DskipTests

# 2. 挂载后台守护进程启动 (免疫 SIGHUP)
nohup java -jar target/mock-crossborder-gateway-1.0-SNAPSHOT.jar &
