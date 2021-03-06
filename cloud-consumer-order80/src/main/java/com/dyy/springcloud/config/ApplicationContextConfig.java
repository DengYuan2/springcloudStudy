package com.dyy.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextConfig {

    //（多机版且）provider也集群时，要赋予RestTemplate负载均衡的能力，使用@LoadBalanced注解，实现负载均衡，默认的是轮询机制，不过也可以自己选择
    @Bean
//    @LoadBalanced //使用自定义的负载均衡算法时，需要将其注释掉
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
