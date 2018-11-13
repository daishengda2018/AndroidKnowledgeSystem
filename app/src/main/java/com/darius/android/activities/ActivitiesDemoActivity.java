package com.darius.android.activities;

import android.support.v7.widget.RecyclerView;

import com.darius.android.MainActivity;

/**
 * 所有Activity相关Demo入口
 */
public class ActivitiesDemoActivity extends MainActivity {

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new ActivitiesDemoAdapter(this);
    }
}
