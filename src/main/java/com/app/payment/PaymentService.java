
package com.app.payment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public boolean processSettlement(PaymentNotification request) {
        
        // 核心防御：利用探照灯去金库查重
        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            System.out.println("⛔ 拦截重复订单: " + request.getOrderId());
            return false; // 直接打回，拒绝执行后面的保存
        }

        PaymentOrder order = new PaymentOrder();
        order.setOrderId(request.getOrderId());
        order.setAmount(request.getAmount());
        order.setStatus(request.getStatus());
        
       // 第 1 步：写入订单（此时数据暂时存在于内存缓冲区，并未真正落盘）
        paymentRepository.save(order);
        System.out.println("⚠️ 警告：订单已执行 save，但事务尚未提交...");

 
         System.out.println("✅ 订单成功入库: " + request.getOrderId());
        return true;

       
    }
}
