package com.example.dsd.demo.patterns.image_loader.part3.cache;

import android.graphics.Bitmap;

/**
 * 图片缓存层
 * <p>
 * Created by im_dsd on 2020-01-16
 */
public interface ImageCache {
    void put(String url, Bitmap bitmap);

    Bitmap get(String url);
}
