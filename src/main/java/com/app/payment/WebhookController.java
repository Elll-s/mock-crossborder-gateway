package com.app.payment;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@Tag(name = "跨境支付核心通知接口", description = "用于接收海外网关的资金落盘通知")
public class WebhookController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "处理支付回调", description = "验证Token，执行幂等校验，并保证ACID事务入库")
    @PostMapping("/api/payment/notify")
    public ResponseEntity<String> receiveNotification(
            @RequestHeader(value = "X-Payment-Token", required = false) String token, 
            @RequestBody PaymentNotification request) {
        
        if (!"Secret_778899".equals(token)) {
            System.out.println("⚠️ 遭到黑客攻击！");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ILLEGAL_REQUEST");
        }
	// 核心安检逻辑 (Token 校验下面加这段)
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("金额必须大于 0 ！黑客滚粗！");
        }

        System.out.println("🚨 前台收到数据，正在移交业务层处理...");
	// 接收业务经理的审判结果
    boolean isSuccess = paymentService.processSettlement(request);
    
    if (!isSuccess) {
        // TODO 2: 填入你查到的那个代表 Conflict 的状态码
        return ResponseEntity.status(HttpStatus.CONFLICT).body("DUPLICATE_ORDER");
    }
    
    return ResponseEntity.ok("SUCCESS");        
         
    }
}
