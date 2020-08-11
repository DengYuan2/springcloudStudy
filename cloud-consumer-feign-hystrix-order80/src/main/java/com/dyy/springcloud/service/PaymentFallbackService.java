package com.dyy.springcloud.service;

import org.springframework.stereotype.Component;

/**
 * 为了解决降级中的代码耦合的问题，实现feign的接口，在此统一处理异常
 * 它的运行逻辑是:
 *         当请求过来,首先还是通过Feign远程调用pay模块对应的方法
 *     但是如果pay模块报错,调用失败,那么就会调用PayMentFalbackService类的
 *     当前同名的方法,作为降级方法
 * 不过，这样虽然解决了代码耦合度问题,但是又出现了过多重复代码的问题,每个方法都有一个降级方法
 */
@Component
public class PaymentFallbackService implements PaymentHystrixService{
    @Override
    public String paymentInfo_OK(Integer id) {
        return "------PaymentFallbackService fall back -paymentInfo_OK,/(ㄒoㄒ)/~~";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "------PaymentFallbackService fall back -paymentInfo_TimeOut ,/(ㄒoㄒ)/~~";
    }
}
