package com.example.dsd.demo.ui.views.other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * todo
 * Created by im_dsd on 2019-10-08
 */
public class GestureDetectorDemo extends FrameLayout implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private GestureDetector mGestureDetector = new GestureDetector(this);

    public GestureDetectorDemo(@NonNull Context context) {
        super(context);
        // 设置是否允许长按。如果允许长按，当用户按下并保持按下状态时， 将收到一个长按事件，同时不再接收其它事件；
        // 如果禁用长按， 当用户按下并保持按下状态然后再移动手指时，将会接收到滚动事件。 长按默认为允许。
        // 默认是 true ，设为 false 后可以解决长按无法拖动的问题。
        mGestureDetector.setIsLongpressEnabled(false);
        mGestureDetector.setOnDoubleTapListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 监听事件
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // 点击事件，类似于 ACTION_DOWN 事件。
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // 单击事件，相当于 ACTION_DOWN + ACTION_UP
        return false;
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // 拖动，ACTION_DOWN + 一些列 ACTION_MOVE 强调的是滑动事件。
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // 长按
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // 快速滑动，用户按下屏幕，快速滑动后松开，相当于：ACTION_DOWN + 一些列 ACTION_MOVE + ACTION_UP
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
