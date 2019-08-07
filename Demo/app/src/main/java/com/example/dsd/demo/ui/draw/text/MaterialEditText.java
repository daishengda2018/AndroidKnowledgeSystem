package com.example.dsd.demo.ui.draw.text;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.example.dsd.demo.utils.DisplayUtils;

/**
 * todo
 * Created by im_dsd on 2019-08-07
 */
public class MaterialEditText extends android.support.v7.widget.AppCompatEditText {

    public MaterialEditText(Context context) {
        this(context, null);
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MaterialEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) DisplayUtils.dp2px(20);
        int width = resolveSize(size, widthMeasureSpec);
        int height = resolveSize(size, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
