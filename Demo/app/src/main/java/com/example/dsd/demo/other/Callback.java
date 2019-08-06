package com.example.dsd.demo.other;

/**
 * @author DSD on 04/01/2019
 * @see
 * @since
 */
public interface Callback<T> {
    void onResult(T result);

    void onError(Exception e);
}
