package com.example.dsd.demo.pattern.proxy.dynamic;

/**
 * 动态代理接口的实现类
 *
 * Created by im_dsd on 2019-08-26
 */
public class DynamicProxy implements IDynamicProxy {
    @Override
    public String name() {
        return "DSD";
    }

    @Override
    public int age() {
        return 25;
    }
}
