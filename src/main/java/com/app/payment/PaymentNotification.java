
package com.app.payment;

import com.fasterxml.jackson.annotation.JsonProperty; // 导入你查到的魔法贴纸

public class PaymentNotification {
    
    @JsonProperty("order_id") // 贴上魔法贴纸，解决命名冲突
    private String orderId; // 填入符合 Java 规范的骆驼拼写法变量名
    
    private int amount;
    private String status;

    // 工业铁律：必须为所有变量生成 Get 和 Set 方法！
    // (在 nano 里手动敲出这几个方法，虽然有点痛苦，但这能强化你的肌肉记忆)
    public String getOrderId() { return this.orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public int getAmount() { return this.amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }
}
