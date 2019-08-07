package com.example.dsd.demo.other;

/**
 * @author DSD on 04/01/2019
 * @param <T> 数据类型
 */
public interface Callback<T> {

    void onResult(T result);

    void onError(Exception e);
}
