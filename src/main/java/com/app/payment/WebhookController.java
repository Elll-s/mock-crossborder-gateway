package com.app.payment;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // 导入投递员
import com.fasterxml.jackson.databind.ObjectMapper; // 导入 JSON 序列化工具
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@Tag(name = "跨境支付核心通知接口", description = "用于接收海外网关的资金落盘通知")
public class WebhookController {

    @Autowired
    private RabbitTemplate rabbitTemplate; // 呼叫 RabbitMQ 投递员

    @Autowired
    private ObjectMapper objectMapper; // Spring 自带的 JSON 转换工具

    @Operation(summary = "处理支付回调", description = "接收请求后瞬间推入 RabbitMQ，实现异步削峰")
    @PostMapping("/api/payment/notify")
    public ResponseEntity<String> receiveNotification(
            @RequestHeader("X-Payment-Token") String token,
            @RequestBody PaymentNotification request) throws Exception {

        // 1. 模拟鉴权 (原封不动)
        if (!"Secret_778899".equals(token)) {
            return ResponseEntity.status(401).body("UNAUTHORIZED");
        }

        // 2. 极其暴力的异步解耦：把对象转成 JSON 字符串，直接“砸”进队列！
        String message = objectMapper.writeValueAsString(request);
        rabbitTemplate.convertAndSend("payment_queue", message);

        System.out.println("🚀 前台极速响应：订单 " + request.getOrderId() + " 已扔进 RabbitMQ 队列！");

        // 3. 0.001秒内瞬间返回给俄罗斯网关，绝不阻塞！
        return ResponseEntity.ok("SUCCESS");
    }
}
