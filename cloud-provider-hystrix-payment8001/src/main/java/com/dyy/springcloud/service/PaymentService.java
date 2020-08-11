package com.dyy.springcloud.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.TimeUnit;

/**
 * 应该先有一个接口的，不过为了节省时间，此处就跳过了，只搞一个实现类
 */
@Service
public class PaymentService {

    /**
     * 正常访问，肯定OK的方法
     * @param id
     * @return
     */
    public String paymengInfo_OK(Integer id){
        return "线程池："+Thread.currentThread().getName()+" paymentInfo_OK，id:"+id+"\t"+"O(∩_∩)O 哈哈~";
    }

    /**
     * 一个会超时的方法，没有加上服务降级的时候
     * @param id
     * @return
     */
//    public String paymengInfo_TimeOut(Integer id){
//        int timeNumber=5;
//        try {
//            TimeUnit.SECONDS.sleep(timeNumber);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return "线程池："+Thread.currentThread().getName()+" paymentInfo_Timeout，id:"+id+"\t"+"O(∩_∩)O 哈哈~"+" 耗时"+timeNumber+"秒钟";
//    }

    //加上服务降级,如果该方法出错或超时了，就用paymentInfo_TimeOutHandler方法兜底
    //服务降级一般是放在客户端，也就是order模块
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "5000") //设置自身调用超时时间的峰值（演示降级时此处可设为2秒），超过了就用兜底方法
    })
    public String paymengInfo_TimeOut(Integer id){
        int timeNumber=3;
        try {
            TimeUnit.SECONDS.sleep(timeNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池："+Thread.currentThread().getName()+" paymentInfo_Timeout，id:"+id+"\t"+"O(∩_∩)O 哈哈~"+" 耗时"+timeNumber+"秒钟";
    }

    //只要服务不可用，都会跳到此方法中来
    public String paymentInfo_TimeOutHandler(Integer id){
        return "线程池："+Thread.currentThread().getName()+" 8001系统繁忙或运行报错，请稍后再试~~，id:"+id+"\t"+" /(ㄒoㄒ)/~~ ";

    }

    //若不是超时，而是报错，也会跳到兜底方法中去
//    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler",commandProperties = {
//            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "3000") //设置自身调用超时时间的峰值（此处为3秒），超过了就用兜底方法
//    })
//    public String paymengInfo_TimeOut(Integer id){
//        int age =10/0;
//        return "线程池："+Thread.currentThread().getName()+" paymentInfo_Timeout，id:"+id+"\t"+"O(∩_∩)O 哈哈~";
//    }

}
