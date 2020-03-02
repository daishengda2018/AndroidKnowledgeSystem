package com.example.dsd.demo.patterns.image_loader.part1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 图片加载器
 * <p>
 * Created by im_dsd on 2020-01-15
 */
public class ImageLoader {

    private final LruCache<String, Bitmap> mImageCache;
    private final ExecutorService mExecutorService;

    public ImageLoader() {
        mExecutorService = new ThreadPoolExecutor(1,
            Runtime.getRuntime().availableProcessors(), 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("image_loader_thread");
                return thread;
            });
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

    public void displayImage(final String url, final ImageView view) {
        view.setTag(url);
        mExecutorService.submit(() -> {
            Bitmap bitmap = downloadImage(url);
            if (bitmap == null) {
                return;
            }
            if (view.getTag().equals(url)) {
                updateImage(view, bitmap);
            }
            mImageCache.put(url, bitmap);
        });
    }

    private Bitmap downloadImage(final String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void updateImage(final ImageView view, final Bitmap bitmap) {
        if (view != null) {
            view.post(() -> view.setImageBitmap(bitmap));
        }
    }
}
