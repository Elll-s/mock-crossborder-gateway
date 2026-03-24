package com.app.payment;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate; // 导入武器库
import java.util.concurrent.TimeUnit; // 导入时间单位

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StringRedisTemplate redisTemplate; // 提取 Redis 机械臂

    @Transactional
    public boolean processSettlement(PaymentNotification request) {
        // 给存入 Redis 的钥匙加一个极其专业的前缀，防止和系统里的其他数据撞车
        String redisKey = "ORDER_PROCESSED:" + request.getOrderId();

        // 核心防线 1：Redis 纯内存原子性拦截 (SETNX)
        // 尝试写入内存。附带 TTL 自毁机制（24小时后自动抹除，节约昂贵的内存）
        Boolean isNewOrder = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);

        // 如果返回 false，说明 1 毫秒前已经有同样的请求拿到了锁
        if (Boolean.FALSE.equals(isNewOrder)) {
            System.out.println("🛡️ Redis 内存神盾极速拦截重复订单: " + request.getOrderId());
            return false; // 直接打回，连 MySQL 的门都不准碰！
        }

        // 核心防线 2：MySQL 物理兜底拦截
        // 万一 Redis 突然重启导致内存丢失，用数据库进行最后的绝对防御
        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            System.out.println("⚠️ MySQL 兜底拦截重复订单: " + request.getOrderId());
            return false;
        }

        // 最终落盘
        PaymentOrder order = new PaymentOrder();
        order.setOrderId(request.getOrderId());
        order.setAmount(request.getAmount());
        order.setStatus(request.getStatus());
        paymentRepository.save(order);
        
        System.out.println("✅ 订单成功落盘: " + request.getOrderId());
        return true;
    }
}
