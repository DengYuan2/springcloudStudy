package com.dyy.springcloud.controller;

import com.dyy.springcloud.entities.CommonResult;
import com.dyy.springcloud.entities.Payment;
import com.dyy.springcloud.service.PaymentService;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    //value值即application.yml中的信息
    @Value("${server.port}")
    private String serverPort;

    //服务发现，从eureka注册中心拿到其中服务的信息
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping("/payment/create")
    public CommonResult create(@RequestBody Payment payment){
        int result =paymentService.create(payment);//返回结果>0则代表插入成功
        log.info("插入结果："+result);
        if (result>0){
            return new CommonResult(200,"插入数据库成功，serverPort："+serverPort,result);
        }else {
            return new CommonResult(444,"插入数据库失败",null);
        }

    }

    @GetMapping("/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id")Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("查询结果："+payment);
        if (payment!=null){
            return new CommonResult(200,"查询成功，serverPort："+serverPort,payment);
        }else {
            return new CommonResult(444,"没有对应记录，查询ID："+id,null);
        }
    }


    //演示，自己访问自己所在注册中心，看看能拿到些什么
    @GetMapping("/payment/discovery")
    public Object discovery(){
        //查看服务清单列表
        List<String> services = discoveryClient.getServices();
        for (String element:services             ) {
            log.info("*****element:"+element);
        }

        //拿到具体服务的信息
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance: instances             ) {
            log.info(instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }
        return this.discoveryClient;

    }

    //仿照默认的轮询算法，自定义负载均衡算法，并使用
    @GetMapping("/payment/lb")
    public String getPaymentLB(){
        return serverPort;
    }

    //为了演示OpenFeign的超时设置【provider这里】
    @GetMapping("/payment/feign/timeout")
    public String paymentFeignTimeout(){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }
}
