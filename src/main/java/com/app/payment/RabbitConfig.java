package com.app.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitConfig {
    // 自动在 RabbitMQ 中创建一个名为 "payment_queue" 的持久化收件箱
    @Bean
    public Queue paymentQueue() {
        return new Queue("payment_queue", true); 
    }
}
