package com.darius.android.fragments

import android.support.v7.widget.RecyclerView
import com.darius.android.MainActivity

class FragmentDemoListActivity : MainActivity() {

    override fun getAdapter(): RecyclerView.Adapter<*> {
        return FragmentsDemoAdapter(this)
    }
}
