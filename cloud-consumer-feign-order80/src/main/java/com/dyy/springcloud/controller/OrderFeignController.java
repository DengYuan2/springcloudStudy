package com.dyy.springcloud.controller;

import com.dyy.springcloud.entities.CommonResult;
import com.dyy.springcloud.entities.Payment;
import com.dyy.springcloud.service.PaymentFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class OrderFeignController {
    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id")Long id){
        return paymentFeignService.getPaymentById(id);
    }

    //为了演示OpenFeign的超时设置【consumer这里2】
    @GetMapping("consumer/payment/feign/timeout")
    public String paymentFeignTimeout(){
        //openfeign-ribbon，客户端一般默认等待1秒钟，不过provider侧故意设置为了3秒钟
        //所以如果application.yml钟没有关于ribbon关于时间的设置，consumer端调用该方法的话会报错哦
        return paymentFeignService.paymentFeignTimeout();

    }

}
