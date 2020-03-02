package com.example.dsd.demo.patterns.image_loader.part2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 对图片加载器实行单一原则修改，抽取 cache 层逻辑
 * <p>
 * Created by im_dsd on 2020-01-15
 */
public class ImageLoader {
    private final ImageCache mImageCache = new ImageCache();
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
