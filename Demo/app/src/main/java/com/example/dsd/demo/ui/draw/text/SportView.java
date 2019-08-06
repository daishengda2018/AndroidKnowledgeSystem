package com.example.dsd.demo.ui.draw.text;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 对应Hencoder plus 第10节课程：绘制文字
 * <p>
 * Created by im_dsd on 2019-07-09
 */
public class SportView extends View {

    private static final float RADIUS = DisplayUtils.dp2px(100);
    private static final float WIDTH = DisplayUtils.dp2px(20);
    private static final String TEXT = "abab";
    private Paint.FontMetrics mFontMetrics;


    public SportView(Context context) {
        super(context);
    }

    public SportView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Paint mPaint;

    private Rect mTextBounds;

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#f2f2f2"));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(WIDTH);
        mPaint.setTextSize(DisplayUtils.sp2px(40));
        mTextBounds = new Rect();
        mPaint.getTextBounds(TEXT,0,  TEXT.length(), mTextBounds);
        mFontMetrics = mPaint.getFontMetrics();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#f2f2f2"));

        // 绘制背景圆环
        float cx = getWidth() * 0.5f;
        float cy = getHeight() * 0.5f;
        canvas.drawCircle(cx, cy, RADIUS, mPaint);

        // 绘制进度条
        mPaint.setColor(Color.parseColor("#ff4081"));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(cx - RADIUS, cy - RADIUS, cx + RADIUS, cy + RADIUS
            , -120, 225, false, mPaint);

        // 绘制文字
        canvas.drawText(TEXT, cx , cy - (mFontMetrics.ascent + mFontMetrics.descent) *0.5f, mPaint);
    }
}
