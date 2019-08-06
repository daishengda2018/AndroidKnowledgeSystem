package com.example.dsd.demo.pattern.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * todo
 * Created by im_dsd on 2019/4/12
 */
public class DynamicProxyDemo {
    public static void main (String[] args) {
        Class<DynamicProxyInterface> clazz = DynamicProxyInterface.class;
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        };
        DynamicProxyInterface instance = (DynamicProxyInterface) Proxy.newProxyInstance(clazz.getClassLoader(),
                                                             new Class[]{clazz}, invocationHandler);

        instance.age();
    }
}
