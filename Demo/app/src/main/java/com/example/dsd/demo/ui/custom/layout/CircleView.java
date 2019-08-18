package com.example.dsd.demo.ui.custom.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 自定义 View 简单测量
 * Created by im_dsd on 2019-08-15
 */
public class CircleView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float PADDING = DisplayUtils.dp2px(20);
    private static final float RADIUS = DisplayUtils.dp2px(80);


    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 没有必要再让 view 自己测量一遍了，浪费时间
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) ((PADDING + RADIUS) * 2);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int result = 0;
        switch (widthMode) {
            // 不超过
            case MeasureSpec.AT_MOST:
                if (widthSize < size) {
                    result = widthSize;
                } else {
                    result = size;
                }
                break;
            // 精准的,
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
            // 无限大，没有指定大小
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            default:
                result = 0;
                break;
        }


        // 指定最小的 size
        int width = resolveSize(size, widthMeasureSpec);
        int height = resolveSize(size, heightMeasureSpec);

        // 设置大小
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(PADDING + RADIUS, PADDING + RADIUS, RADIUS, mPaint);
    }
}
