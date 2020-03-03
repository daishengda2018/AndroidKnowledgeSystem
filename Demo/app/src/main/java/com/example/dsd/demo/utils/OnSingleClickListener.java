package com.example.dsd.demo.utils;

import android.view.View;

/**
 * 防抖动点击事件
 * <p>
 * Created by im_dsd on 2019-12-18
 */
public abstract class OnSingleClickListener implements View.OnClickListener {
    /**
     * 最大点击间隔事件
     */
    protected static final int MAX_CLICK_TIME = 300;
    /**
     * 允许两次点击的最小时间差
     */
    protected final int mClickTimeThreshold;

    protected OnSingleClickListener() {
        this(MAX_CLICK_TIME);
    }

    protected OnSingleClickListener(int clickThreshold) {
        mClickTimeThreshold = clickThreshold;
    }

    /**
     * 不是快速的两次点击
     *
     * @param view
     * @return
     */
    protected boolean isNotFastDoubleClick(View view) {
        if (null == view) {
            // 如果view是空的话  那么默认是一次正常的点击
            return true;
        }
        Object tag = view.getTag(view.getId());
        long currentClickTime = System.currentTimeMillis();
        view.setTag(view.getId(), currentClickTime);
        if (null != tag) {
            long lastClickTime = (long) tag;
            // 两次点击的时间差
            long duringTime = currentClickTime - lastClickTime;
            return duringTime >= mClickTimeThreshold;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (isNotFastDoubleClick(v)) {
            onSingleClick(v);
        }
    }

    /**
     * 单次点击事件
     *
     * @param view
     */
    public abstract void onSingleClick(View view);
}
