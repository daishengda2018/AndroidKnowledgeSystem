package com.darius.android;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author DSD on 12/11/2018
 * @see
 * @since
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.CategoryVH> {

    private final Context mContext;
    private final String[] mTitleArray;

    public MainRecyclerAdapter(Context context) {
        mContext = context;
        mTitleArray = mContext.getResources().getStringArray(R.array.category_titles);
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View inflate = layoutInflater.inflate(R.layout.category_item_layout, viewGroup, false);
        return new CategoryVH(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH categoryVH, int i) {
        categoryVH.attach(mTitleArray[i]);
    }

    @Override
    public int getItemCount() {
        return mTitleArray.length;
    }


    /**
     * Categor yVH
     */
    public static class CategoryVH extends RecyclerView.ViewHolder {

        private final TextView mTitleTv;

        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.category_title_tv);
        }

        public void attach(final String str) {
            String[] split = str.split("\\.");
            mTitleTv.setText(split[split.length - 1]);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Class<?> aClass = null;
                    try {
                        aClass = Class.forName(str);
                        Intent intent = new Intent(itemView.getContext(), aClass);
                        itemView.getContext().startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}


