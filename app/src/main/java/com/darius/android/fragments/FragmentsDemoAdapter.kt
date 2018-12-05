package com.darius.android.fragments

import android.content.Context
import android.content.Intent
import android.view.View
import com.darius.android.MainRecyclerAdapter
import com.darius.android.R

/**
 * Activities Demo Adapter
 * @author DSD on 13/11/2018
 * @see
 * @since
 */
class FragmentsDemoAdapter(context: Context) : MainRecyclerAdapter(context) {
    companion object {
        const val DATA: String = "FRAGMENT_NAME";
    }

    override val stringArrayRes: Int = R.array.category_titles

    class FragmentsCategoryVH(itemView: View) : CategoryVH(itemView) {
        override fun getIntent(str: String): Intent {
            val intent = Intent(itemView.context, FragmentsContainerActivity::class.java)
            intent.putExtra(DATA, intent)
            return intent
        }
    }

}