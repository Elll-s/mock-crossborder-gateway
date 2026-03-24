package com.app.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class) // 回答问题 2
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        
        // 把红色的恐怖报错，拦截下来，变成体面的内部日志
        System.out.println("🛡️ 全局防御网启动，成功拦截非法请求，原因：" + e.getMessage());
        
        // 给俄罗斯网关返回一个极其标准的、体面的、我们自己定义的 400 错误 JSON 字符串
        String safeResponse = "{\"status\": \"FAILED\", \"error_code\": \"INVALID_AMOUNT\", \"message\": \"Amount must be greater than zero.\"}";
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(safeResponse);
    }
}
