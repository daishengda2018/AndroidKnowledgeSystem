package com.example.dsd.demo.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 自定义View ：仪表盘
 * Created by im_dsd on 2019-05-22
 */
public class DashBoard extends View {
    /***半径*/
    private static final int RADIUS = (int) DisplayUtils.dp2px(150);
    /***指针长度*/
    private static final int POINTER_LENGTH = (int) DisplayUtils.dp2px(100);
    /***起始角度*/
    private static final int START_ANGLE = 120;
    /**刻度数量*/
    private static final float MARK_COUNT = 20f;
    private Path mDashPath;
    private Paint mDashPaint;
    private RectF mRectF;
    private int mSweepAngle;
    private final Paint mArcPaint;
    private Path mArcPath;
    private int mHalfWidth;
    private int mHalfHeight;

    public DashBoard(Context context) {
        super(context);
    }

    public DashBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DashBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DashBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        mDashPath = new Path();
        mArcPath = new Path();
        mDashPath.addRect(0, 0, DisplayUtils.dp2px(2), DisplayUtils.dp2px(10), Path.Direction.CCW);
        mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashPaint.setColor(Color.BLACK);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setStrokeWidth(DisplayUtils.dp2px(2));
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(Color.BLACK);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(DisplayUtils.dp2px(2));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHalfWidth = w / 2;
        mHalfHeight = h / 2;
        mRectF = new RectF(mHalfWidth - RADIUS, mHalfHeight - RADIUS, mHalfWidth + RADIUS, mHalfHeight + RADIUS);
        mSweepAngle = 360 - START_ANGLE / 2;
        mArcPath.addArc(mRectF, START_ANGLE, mSweepAngle);
        PathMeasure pathMeasure = new PathMeasure(mArcPath, false);
        float advance =  (pathMeasure.getLength() - DisplayUtils.dp2px(2)) / MARK_COUNT;
        mDashPaint.setPathEffect(new PathDashPathEffect(mDashPath, advance, 0 , PathDashPathEffect.Style.ROTATE));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the dial
        canvas.drawArc(mRectF, START_ANGLE,  mSweepAngle, false, mDashPaint);
        // draw the dash line
        canvas.drawArc(mRectF, START_ANGLE,  mSweepAngle, false, mArcPaint);
        // draw the pointer
        float stopX = mHalfWidth + (float) Math.cos(Math.toRadians(getAngleForMark(5))) * POINTER_LENGTH;
        float stopY = mHalfHeight + (float) Math.sin(Math.toRadians(getAngleForMark(5))) * POINTER_LENGTH;
        canvas.drawLine(mHalfWidth, mHalfHeight , stopX, stopY, mArcPaint);
    }

    /**
     * 获的角度
     * @param mark 需要指向的刻度
     */
    private float getAngleForMark(int mark) {
        return START_ANGLE + (mSweepAngle / MARK_COUNT * mark);
    }
}
