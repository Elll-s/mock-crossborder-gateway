package com.app.payment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PaymentWebhook{
public static void main(String[]args){
String jsonPayload="{\"order_id\":\"RUB8900\",\"amount\":8900,\"status\":\"SUCCESS\"}";

JsonObject jsonobject=JsonParser.parseString(jsonPayload).getAsJsonObject();
String order_id=jsonobject.get("order_id").getAsString();
int amount=jsonobject.get("amount").getAsInt();
String status=jsonobject.get("status").getAsString();

System.out.println("order_id:"+order_id+"\n"+"amount:"+amount+"\n"+"status:"+status);
}
}
