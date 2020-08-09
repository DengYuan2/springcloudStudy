package com.dyy.springcloud.controller;

import com.dyy.springcloud.entities.CommonResult;
import com.dyy.springcloud.entities.Payment;
import com.dyy.springcloud.lb.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.ConnectableFlux;

import javax.annotation.Resource;
import javax.security.auth.login.Configuration;
import java.net.URI;
import java.util.List;

@RestController
@Slf4j
public class OrderController {
    //这是单机版时，写死没关系
//    public static final String PAYMENT_URL="http://localhost:8001";

    //但是多机版时，只认微服务名称（即yml中spring.application.name），不认ip和端口，实现负载均衡
    public static final String PAYMENT_URL="http://CLOUD-PAYMENT-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    //引入自定义的负载均衡算法，也就是仿照轮询源码自己写的轮询算法
    @Resource
    LoadBalancer loadBalancer;

    @Resource
    DiscoveryClient discoveryClient;

    //调用微服务提供者
    @GetMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment){
        return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);

    }

    //getForObject得到的是json串
    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id")Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id, CommonResult.class);

    }

    //讲解RestTemplate中getForObject和getForEntity的区别
    //需要更详细的信息的话可以用getForEntity
    @GetMapping("/consumer/payment/getForEntity/{id}")
    public CommonResult<Payment> getPayment2(@PathVariable("id") Long id){
        ResponseEntity<CommonResult> entity = restTemplate.getForEntity(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        if (entity.getStatusCode().is2xxSuccessful()){
            log.info(entity.getStatusCode()+"\t"+entity.getHeaders());
            return entity.getBody();
        }else {
            return new CommonResult<>(444,"操作失败");
        }
    }

    @GetMapping("/consumer/payment/lb")
    public String getPaymentLB(){
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        if (instances==null||instances.size()<=0){
            return null;
        }
        ServiceInstance instance = loadBalancer.instance(instances);
        URI uri = instance.getUri();
        return restTemplate.getForObject(uri+"/payment/lb",String.class);

    }

}
