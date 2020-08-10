package com.dyy.springcloud.service;

import com.dyy.springcloud.entities.CommonResult;
import com.dyy.springcloud.entities.Payment;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "CLOUD-PAYMENT-SERVICE") //value值为eureka下的微服务名称
public interface PaymentFeignService {
    @GetMapping("/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id")Long id);

    //为了演示OpenFeign的超时设置【consumer这里1】
    @GetMapping("/payment/feign/timeout")
    public String paymentFeignTimeout();

}
