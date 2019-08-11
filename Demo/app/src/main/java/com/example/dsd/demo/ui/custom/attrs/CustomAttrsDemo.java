package com.example.dsd.demo.ui.custom.attrs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.dsd.demo.R;

/**
 * 自定义属性 Demo
 *
 * Created by im_dsd on 2019-08-11
 */
public class CustomAttrsDemo extends android.support.v7.widget.AppCompatTextView {

    private final int mTextColor;
    private final int mTextSize;

    public CustomAttrsDemo(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomAttrsDemo);
        mTextColor = array.getColor(R.styleable.CustomAttrsDemo_textColor, Color.BLACK);
        mTextSize = array.getDimensionPixelSize(R.styleable.CustomAttrsDemo_textSize, 18);
        // 注意使用完成之后一定要回收
        array.recycle();
    }

}
