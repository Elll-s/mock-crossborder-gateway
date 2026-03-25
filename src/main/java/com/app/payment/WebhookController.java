package com.app.payment;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@Tag(name = "跨境支付核心通知接口", description = "带 HMAC-SHA256 验签的安全网关")
public class WebhookController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 假设这是你和 YooMoney 线下约定的绝对机密（绝不能泄露）
    private static final String SECRET_KEY = "YooMoney_Secret_Key_2026";

    @Operation(summary = "处理支付回调", description = "执行 HMAC-SHA256 验签后推入队列")
    @PostMapping("/api/payment/notify")
    public ResponseEntity<String> receiveNotification(
            @RequestHeader("X-Signature") String clientSignature, // 现在的暗号变成了加密签名！
            @RequestBody String rawJson) { // 必须用 String 接收原汁原味的 JSON 文本

        try {
            // ⚔️ 第一道生死防线：HMAC-SHA256 动态验签
            String myCalculatedSignature = HmacUtils.calculateHmac(rawJson, SECRET_KEY);
            
            // 如果算出来的指纹，和 Header 里传过来的对不上
            if (!myCalculatedSignature.equalsIgnoreCase(clientSignature)) {
                System.out.println("🚨 极度危险：拦截到签名伪造的黑客请求！");
                return ResponseEntity.status(401).body("INVALID_SIGNATURE");
            }

            // 验签通过，说明数据绝对安全！把原始字符串重新变成 Java 对象
            PaymentNotification request = objectMapper.readValue(rawJson, PaymentNotification.class);

            // 扔进 RabbitMQ 队列
            rabbitTemplate.convertAndSend("payment_queue", rawJson);
            System.out.println("🚀 验签通过！订单 " + request.getOrderId() + " 已扔进 RabbitMQ 队列！");

            return ResponseEntity.ok("SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("CRYPTO_ERROR");
        }
    }
}
