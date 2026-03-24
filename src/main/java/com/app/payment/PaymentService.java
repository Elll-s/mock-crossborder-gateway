package com.app.payment;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener; // 导入监听雷达
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 神级注解：只要队列里有消息，后台立刻启动搬运！
    @RabbitListener(queues = "payment_queue") 
    @Transactional
    public void consumePaymentMessage(String message) {
        try {
            // 1. 把 JSON 字符串重新变回 Java 对象
            PaymentNotification request = objectMapper.readValue(message, PaymentNotification.class);
            String orderId = request.getOrderId();
            String redisKey = "ORDER_PROCESSED:" + orderId;

            System.out.println("⏳ 后台消费者开始处理订单: " + orderId);

            // 2. Redis 内存神盾防线
            Boolean isNewOrder = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);
            if (Boolean.FALSE.equals(isNewOrder)) {
                System.out.println("🛡️ 消费者极速拦截重复订单: " + orderId);
                return; // 直接丢弃，结束消费
            }

            // 3. MySQL 落盘兜底
            if (paymentRepository.existsByOrderId(orderId)) {
                return;
            }

            PaymentOrder order = new PaymentOrder();
            order.setOrderId(orderId);
            order.setAmount(request.getAmount());
            order.setStatus(request.getStatus());
            paymentRepository.save(order);
            
            System.out.println("✅ 消费者完成最终落盘: " + orderId);

        } catch (Exception e) {
            System.err.println("❌ 消费失败，触发事务回滚：" + e.getMessage());
            throw new RuntimeException(e); // 报错将触发 ACID 回滚
        }
    }
}
