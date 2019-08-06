package com.example.dsd.demo.other;

import java.util.List;

/**
 * @author DSD on 04/01/2019
 * @see
 * @since
 */
public abstract class AsyncJob {
    public abstract void start(Callback<List<Cat>> callback);
}
