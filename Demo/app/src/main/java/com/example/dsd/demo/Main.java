package com.example.dsd.demo;

import com.example.dsd.demo.handler.HandlerDemo;

/**
 * Java 代码入口
 *
 * Created by im_dsd on 2019-09-05
 */
public class Main {
    public static void main(String[] args) {
//        new TreadLocalDemo().runDemo();
        new HandlerDemo().startThreadHandler();
    }
}
