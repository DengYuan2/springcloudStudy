package com.dyy.springcloud.service;

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
     * 一个会超时的方法
     * @param id
     * @return
     */
    public String paymengInfo_TimeOut(Integer id){
        int timeNumber=3;
        try {
            TimeUnit.SECONDS.sleep(timeNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池："+Thread.currentThread().getName()+" paymentInfo_Timeout，id:"+id+"\t"+"O(∩_∩)O 哈哈~"+" 耗时"+timeNumber+"秒钟";
    }
}
