package com.app.payment;

import jakarta.persistence.*; // 导入 JPA 所有核心注解

@Entity 
@Table(name = "payment_orders") // 精准绑定数据库的表名
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 声明自增
    private Integer id;

    @Column(name = "order_id") // 绑定数据库的蛇形列名
    private String orderId;

    private Integer amount;
    private String status;

    // 必须生成 Get 和 Set 方法！(自己在 nano 里补全它们)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
