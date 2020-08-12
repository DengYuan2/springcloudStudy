package com.dyy.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;
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

    //***上面是服务降级，下面讲服务熔断***************************************************************


    /**
     * 断路器的打开和关闭,是按照一下5步决定的
     *     1,并发此时是否达到我们指定的阈值：requestVolumeThreshold
     *     2,错误百分比,比如我们配置了60%,那么如果并发请求中,10次有6次是失败的,就开启断路器：errorThresholdPercentage
     *     3,上面的条件符合,断路器改变状态为open(开启)
     *     4,这个服务的断路器开启,所有请求无法访问
     *     5,在我们的时间窗口期,期间,尝试让一些请求通过(半开状态),如果请求还是失败,证明断路器还是开启状态,服务没有恢复
     *         如果请求成功了,证明服务已经恢复,断路器状态变为close关闭状态
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = { //兜底方法
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"), //是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"), //请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), //时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60") //失败率达到多少后跳闸/开启断路器
    })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id){
        if (id<0){
            throw new RuntimeException("*****id不能为负数~~"); //出现异常，到兜底方法中去
        }
        String serialNumber= IdUtil.simpleUUID(); //类似于UUID.randomUUID().toString();此处用得是hutool工具包中的方法
        return Thread.currentThread().getName()+"\t"+"调用成功，流水号："+serialNumber;
    }

    public String paymentCircuitBreaker_fallback(@PathVariable("id")Integer id){
        return "id不能为负数，请稍后再试，/(ㄒoㄒ)/~~ id："+id;

    }

}
