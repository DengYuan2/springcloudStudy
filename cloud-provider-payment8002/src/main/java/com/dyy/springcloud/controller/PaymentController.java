package com.dyy.springcloud.controller;

import com.dyy.springcloud.entities.CommonResult;
import com.dyy.springcloud.entities.Payment;
import com.dyy.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    //value值即application.yml中的信息
    @Value("${server.port}")
    private String serverPort;

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

    //根据8001的自己重新写一遍
    @GetMapping("/payment/discovery")
    public DiscoveryClient discovery(){
        List<String> services = discoveryClient.getServices();
        for (String service: services             ) {
            System.out.println("***service:"+service);
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance:instances                 ) {
                System.out.println(instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
            }
        }
        return discoveryClient;

    }

}
