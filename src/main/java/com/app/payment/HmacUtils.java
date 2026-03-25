package com.app.payment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HmacUtils {
    // 这是一个静态的粉碎机方法
    public static String calculateHmac(String data, String key) throws Exception {
        // 1. 召唤 HMAC-SHA256 引擎
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        // 2. 装填你的专属密钥
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        // 3. 将订单数据化为字节灰烬，执行加密粉碎
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // 4. 将字节灰烬拼接成 64 位的 16 进制字符串（这就是数字指纹）
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
