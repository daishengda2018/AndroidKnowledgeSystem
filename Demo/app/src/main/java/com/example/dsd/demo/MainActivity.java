package com.example.dsd.demo;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.dsd.demo.handler.HandlerDemo;

import java.lang.reflect.Method;

/**
 * create by DSD
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Resources mNewResources;
    private AssetManager mAssetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new HandlerDemo().startThreadHandler();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void loadResource() {
        try {
            mAssetManager = AssetManager.class.newInstance();
            Method assetPath = mAssetManager.getClass().getMethod("addAssetPath", String.class);
            // 到指定目录加载资源文件
            assetPath.invoke(mAssetManager, "apk dex 文件的路径");
            Resources superResources = super.getResources();
            // 获取新的资源对象
            mNewResources = new Resources(mAssetManager, superResources.getDisplayMetrics(), superResources.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        return mNewResources == null ? super.getResources() : mNewResources;
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }
}
