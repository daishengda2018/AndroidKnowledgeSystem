package com.example.dsd.demo.patterns.image_loader.part3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.dsd.demo.patterns.image_loader.part3.cache.DoubleCache;
import com.example.dsd.demo.patterns.image_loader.part3.cache.ImageCache;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 在单一原则上进行开闭原则修改
 *
 * 开闭原则：软件中的对象（类、模块、函数等）应该对于拓展是开放的，但是对于修改时关闭的。
 * <p>
 * Created by im_dsd on 2020-01-15
 */
public class ImageLoader {
    private  ImageCache mImageCache = new DoubleCache();
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
    }

    public void displayImage(final String url, final ImageView view) {
        Bitmap result = mImageCache.get(url);
        if (result != null) {
            updateImage(view, result);
            return;
        }
        submitLoadRequest(url, view);
    }

    private void submitLoadRequest(String url, ImageView view) {
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

    public void setImageCache(ImageCache cache) {
        mImageCache = cache;
    }
}
