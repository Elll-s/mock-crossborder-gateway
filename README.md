# 🚀 Cross-Border Payment Gateway (Async & Idempotent Architecture)

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.X-brightgreen.svg)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Async-orange.svg)
![Redis](https://img.shields.io/badge/Redis-Idempotent-red.svg)
![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)

## 📖 Project Overview (项目概述)
本项目是一个专为**跨境支付场景（如 YooMoney、Stripe 等海外网关 Webhook 回调）**设计的核心异步网关。
针对跨国网络环境中极其常见的“网络抖动延迟”、“网关重试导致的重复并发请求”等致命痛点，从零落地了一套具备**绝对幂等性、削峰填谷、动态验签**的微服务底层架构。

## ⚔️ Core Architecture (核心架构特性)

### 1. 🛡️ Absolute Idempotency (绝对幂等双层结界)
针对海外网关并发重试导致的“重复入账”灾难，设计了双层防御体系：
* **L1 内存屏障:** 基于 Redis `SETNX` 构建毫秒级分布式锁，同 `order_id` 请求在第一秒内被瞬间拦截，保护底层数据库。
* **L2 物理兜底:** 基于 MySQL 唯一索引与 `LIMIT 1` 查询构建 ACID 强一致性事务，彻底杜绝脏写漏洞。

### 2. 🌪️ Async Peak Shaving (极速异步削峰)
* **物理撕裂:** 彻底废弃 Controller 直连 Service 的传统同步阻塞模型。
* **吞吐重构:** 前台 Tomcat 接收请求后，利用 **RabbitMQ** 进行消息瞬间投递并返回 `200 OK`响应。后台消费者隐蔽线程根据 MySQL 实际吞吐极限进行平滑拉取，成功在 1C1G 极低配置下抗住 10,000 级并发洪峰，根除线程池雪崩。

### 3. 🔐 Cryptographic Gateway (金融级动态验签)
* 弃用静态明文 Token，防止黑客抓包重放攻击。
* 基于 Java 原生 `javax.crypto.Mac` 库底层实现 **HMAC-SHA256** 加密算法。对每一次支付回调进行严格的数字指纹校验，六亲不认地击毙所有伪造请求。

### 4. 🐳 Cloud-Native Deployment (云原生一键创世)
* 全面拥抱 DevOps，编写轻量级 `Dockerfile` (基于 Alpine Linux)。
* 统御 `docker-compose.yml` 蓝图，利用 Docker 内部 DNS 网络完美解决微服务启动期竞态条件（Race Condition），实现全架构组件的一键秒级部署与销毁。

---

## 🛠️ Tech Stack (技术栈)
* **Core:** Java 17, Spring Boot 3.x
* **Middleware:** RabbitMQ (AMQP), Redis
* **Database:** MySQL 8.0 (Spring Data JPA)
* **Security:** HMAC-SHA256 Cryptography
* **DevOps:** Docker, Docker Compose

---

## 🚀 Quick Start (一键拉起部署)

无需繁琐的环境配置，仅需安装 Docker 引擎，即可在任何纯净 Linux 服务器上一键复现整套架构：

```bash
# 1. Clone the repository
git clone [https://github.com/](https://github.com/)[你的GitHub用户名]/[你的仓库名].git
cd [你的仓库名]

# 2. Compile and Build
mvn clean package -DskipTests

# 3. Boot up the entire architecture (MySQL, Redis, RabbitMQ, Gateway)
sudo docker-compose up -d

# 4. Verify system status
sudo docker-compose ps

##  Performance Testing (压测战果)
使用 Apache Bench (`ab`) 在 AWS EC2 (1 vCPU, 1GB RAM) 物理环境下进行极限压力测试：
* **并发量:** 10,000 瞬时请求
* **穿透率:** 0% (Redis `SETNX` 神盾成功拦截所有重复构造的并发请求)
* **系统状态:** Tomcat 0 线程阻塞，RabbitMQ 展现完美削峰（平稳堆积与异步消费），MySQL CPU 占用及落盘 I/O 稳定，彻底根除雪崩隐患。
