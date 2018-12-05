package com.darius.android

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @author DSD on 12/11/2018
 * @see
 * @since
 */
open class MainRecyclerAdapter(private val mContext: Context) : RecyclerView.Adapter<MainRecyclerAdapter.CategoryVH>() {
    var titleArray: Array<String>

    open val stringArrayRes: Int = R.array.category_titles

    init {
        titleArray = mContext.resources.getStringArray(stringArrayRes)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CategoryVH {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val inflate = layoutInflater.inflate(R.layout.category_item_layout, viewGroup, false)
        return getViewHolder(inflate)
    }

    override fun onBindViewHolder(categoryVH: CategoryVH, i: Int) {
        categoryVH.attach(titleArray[i])
    }

    override fun getItemCount(): Int {
        return titleArray.size
    }

    open fun getViewHolder(inflate: View): CategoryVH {
        return CategoryVH(inflate)
    }

    /**
     * Category yVH
     */
    open class CategoryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mTitleTv: TextView = itemView.findViewById(R.id.category_title_tv)

        fun attach(str: String) {
            val split = str.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            mTitleTv.text = split[split.size - 1]
            itemView.setOnClickListener {
                var aClass: Class<*>? = null
                try {
                    aClass = Class.forName(str)
                    val intent = Intent(itemView.context, aClass)
                    itemView.context.startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}


