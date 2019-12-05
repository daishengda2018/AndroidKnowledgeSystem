package com.example.dsd.demo.pattern.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理 Demo
 * Created by im_dsd on 2019/4/12
 */
public class DynamicProxyDemo {

    public static void main (String[] args) {
        // 创建被代理类的实例
        final DynamicProxy dynamicProxy = new DynamicProxy();
        Class<? extends DynamicProxy> clazz = dynamicProxy.getClass();
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("在方法执行前做点什么");
                System.out.println(method.getName());
                Object invoke = method.invoke(dynamicProxy, args);
                System.out.println("在方法执行后做点什么 " + invoke);
                return invoke;
            }
        };
        IDynamicProxy instance = (IDynamicProxy) Proxy.newProxyInstance(clazz.getClassLoader(),
                                                             clazz.getInterfaces(), invocationHandler);

        instance.age();
    }
}
