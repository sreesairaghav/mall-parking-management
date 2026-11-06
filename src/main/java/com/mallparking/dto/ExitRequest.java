package com.mallparking.dto;
import com.mallparking.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
public class ExitRequest {
    @NotNull private PaymentMethod paymentMethod;
    public PaymentMethod getPaymentMethod(){return paymentMethod;}
    public void setPaymentMethod(PaymentMethod v){paymentMethod=v;}
}
