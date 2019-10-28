package com.example.dsd.demo.ui.touch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * todo
 * Created by im_dsd on 2019-10-26
 */
public class MyLayout2 extends FrameLayout {
    public MyLayout2(@NonNull Context context) {
        super(context);
    }

    public MyLayout2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLayout2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyLayout2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("TOUCH --- MyLayout2  ", " dispatchTouchEvent " + ev.getAction() + "");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("TOUCH --- MyLayout2  ", " onInterceptTouchEvent " + ev.getAction() + "");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TOUCH --- MyLayout2  ", " onTouchEvent " + event.getAction() + "");
        return super.onTouchEvent(event);
    }
}
