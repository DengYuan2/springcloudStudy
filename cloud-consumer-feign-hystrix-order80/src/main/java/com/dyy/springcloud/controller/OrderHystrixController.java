package com.dyy.springcloud.controller;

import com.dyy.springcloud.service.PaymentHystrixService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod") //设置全局降级
public class OrderHystrixController {
    @Resource
    private PaymentHystrixService paymentHystrixService;

    @GetMapping("/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id")Integer id){
        return paymentHystrixService.paymentInfo_OK(id);
    }

    //没有服务降级时
//    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
//    public String paymentInfo_TimeOut(@PathVariable("id")Integer id){
//        return paymentHystrixService.paymentInfo_TimeOut(id);
//    }

    //未讲全局降级之前：
    //加上专门服务降级,如果该方法出错或超时了，就用paymentTimeOutFallbackMethod方法兜底
//    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {  //commandProperties里面可以加多个参数，故用{}
//            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "1500") //设置自身调用超时时间的峰值，超过了就用兜底方法
//    })
//    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
//    public String paymengInfo_TimeOut(@PathVariable("id")Integer id){
//        //如果是报错，而非超时，也就是和1.5秒没有关系时，这时候的结果也是跳到兜底方法中去
//        int age=10/0;
//        String result = paymentHystrixService.paymentInfo_TimeOut(id); //provider端要耗时3秒才有反应，但它自己认为5秒内都正常运行，故provider端没有问题；但此处要求1.5秒内，故会有问题
//        return result;
//    }

    //使用全局降级规定的方法,出错就找类的头上规定的兜底方法
    @HystrixCommand
    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    public String paymengInfo_TimeOut(@PathVariable("id")Integer id){
        int age=10/0;
        String result = paymentHystrixService.paymentInfo_TimeOut(id);
        return result;
    }


    //只要服务不可用，都会跳到此方法中来
    public String paymentTimeOutFallbackMethod(@PathVariable("id")Integer id){
        return "我是消费者80，对方支付系统繁忙，请10秒钟后再试试，或者自己运行出错，请检查自己，/(ㄒoㄒ)/~~ ";

    }


    //****下面是全局fallback(降级)*************************************************************************************************************************************
    public String payment_Global_FallbackMethod(){
        return "Global异常处理，请稍后再试。/(ㄒoㄒ)/~~";
    }
}
