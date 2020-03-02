package com.example.dsd.demo.patterns.image_loader.part3.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 内存缓存
 *
 * Created by im_dsd on 2020-01-16
 */
public class MemoryCache implements ImageCache {
    private final LruCache<String, Bitmap> mImageCache;

    public MemoryCache() {
        // 计算最大可用内存空间
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // bitmap 的大小计算模式特殊，需要手动计算。
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    @Override
    public Bitmap get(String url) {
        return mImageCache.get(url);
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        mImageCache.put(url, bitmap);
    }
}
