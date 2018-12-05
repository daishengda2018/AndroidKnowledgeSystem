package com.darius.android.activities;

import android.content.Context;

import com.darius.android.MainRecyclerAdapter;
import com.darius.android.R;

/**
 * Activities Demo Adapter
 * @author DSD on 13/11/2018
 * @see
 * @since
 */
public class ActivitiesDemoListAdapter extends MainRecyclerAdapter {

    public ActivitiesDemoListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getStringArrayRes() {
        return R.array.activity_names;
    }
}
