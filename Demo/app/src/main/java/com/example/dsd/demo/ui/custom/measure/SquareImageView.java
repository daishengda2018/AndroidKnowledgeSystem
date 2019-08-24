package com.example.dsd.demo.ui.custom.measure;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 自定义正方形 ImageView
 * <p>
 * Created by im_dsd on 2019-08-24
 */
public class SquareImageView extends android.support.v7.widget.AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure 中已经完成了 View 的测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取测量的结果比较后得出最大值
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int size = Math.max(width, height);
        // 将结果设置回去
        setMeasuredDimension(size, size);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        /*
        // 使用宽高的最大值设置边长
        int width = r - l;
        int height = b - t;
        int size = Math.max(width, height);
        super.layout(l, t, l + size, t + size);
         */
    }
}
