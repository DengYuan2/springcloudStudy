package com.dyy.springcloud;

import myrule.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@EnableEurekaClient
@SpringBootApplication
//@RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration = MySelfRule.class) //启用自定义的负载均衡方法，告诉要访问的是eureka注册中心的CLOUD-PAYMENT-SERVICE服务
public class OrderMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderMain80.class,args);
    }
}
