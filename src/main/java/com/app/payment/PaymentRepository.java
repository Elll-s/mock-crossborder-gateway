package com.app.payment;

import org.springframework.data.jpa.repository.JpaRepository; 


public interface PaymentRepository extends JpaRepository<PaymentOrder, Integer> {
boolean existsByOrderId(String orderId);    
}
