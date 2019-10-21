package com.example.dsd.demo.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * View 触摸事件 Demo
 *
 * Created by im_dsd on 2019-10-21
 */
public class TouchEventDemo extends ViewGroup {
    private float mLastTouchX;
    private float mLastTouchY;
    private boolean isParentNeedEvent = true;

    public TouchEventDemo(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * 滑动冲突解决方案 1 ：外部拦截法
     * 在父 View 中重写方法。
     */
    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // ACTION_DOWN 事件必须返回 false，否则以后所有的事件都会被自身器拦截，子 View 无法接受到事件
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isParentNeedEvent) {
                    intercept = true;
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 必须返回 fasle，否者子 View 不会执行 onClick()
                intercept = false;
                break;
            default:
                break;
        }
        mLastTouchX = x;
        mLastTouchY = y;
        return intercept;
    }

    /**
     * 解决方案 2 part 1 ：内部拦截法
     * 在子 View 中重写方法。
     */
    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isParentNeedEvent) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                break;
        }
        mLastTouchX = x;
        mLastTouchY = y;

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 内部拦截法：part 2
     * 在父 View 中重写方法。
     */
    /*
    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_DOWN == ev.getAction()) {
            return false;
        } else {
            return true;
        }
    }
    */
}
