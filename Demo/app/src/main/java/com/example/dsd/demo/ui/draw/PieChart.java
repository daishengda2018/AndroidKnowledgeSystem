package com.example.dsd.demo.ui.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 扇形图
 *
 * Created by im_dsd on 2019-06-09
 */
public class PieChart extends View {
    /***半径*/
    private static final int RADIUS = (int) DisplayUtils.dp2px(150);
    private RectF mArcRectF;

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Paint mPaint;

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float halfWidth = w * 0.5f;
        float halfHeight = h * 0.5f;
        // set arc draw rect
        mArcRectF = new RectF(halfWidth - RADIUS, halfHeight - RADIUS,
            halfWidth + RADIUS, halfHeight + RADIUS);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(100, 100);
        canvas.drawArc(mArcRectF, 0, 90, true, mPaint);
        canvas.restore();

    }
}
