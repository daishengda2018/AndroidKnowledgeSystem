package com.example.dsd.demo.ui.views.other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.FrameLayout;

/**
 * todo
 * Created by im_dsd on 2019-10-08
 */
public class VelocityTrackerDemo extends FrameLayout {
    public VelocityTrackerDemo(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        // 将事件加入到追踪器
        velocityTracker.addMovement(event);
        // 调用计算代码，这个是必须的，指定计算时间间隔为 1s
        velocityTracker.computeCurrentVelocity(1000);
        // 获取 x、y 轴的速度
        float xVelocity = velocityTracker.getXVelocity();
        float yVelocity = velocityTracker.getYVelocity();
        // 释放跟踪器
        velocityTracker.recycle();
        // 重置并回收内存
        velocityTracker.clear();
        return super.onTouchEvent(event);
    }
}
