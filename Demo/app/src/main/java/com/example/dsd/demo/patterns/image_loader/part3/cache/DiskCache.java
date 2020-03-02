package com.example.dsd.demo.patterns.image_loader.part3.cache;

import android.graphics.Bitmap;

import java.io.File;
import java.util.concurrent.Executor;

import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.io.FileSystem;

/**
 * 图片缓存
 * <p>
 * Created by im_dsd on 2020-01-16
 */
public class DiskCache implements ImageCache {
    FileSystem fileSystem;
    File directory;
    Executor executor;
    private  DiskLruCache mCache;

    public DiskCache() {
        // 计算最大可用内存空间
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
//        mCache = new DiskLruCache(,fileSystem directory, , , , executor);

    }

    @Override
    public Bitmap get(String url) {
//        return mCache.get(url);
        return null;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
//        mCache.put(url, bitmap);
    }
}
