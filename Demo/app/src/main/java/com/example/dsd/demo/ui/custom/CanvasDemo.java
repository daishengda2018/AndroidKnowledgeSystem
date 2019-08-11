package com.example.dsd.demo.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Canvas 裁切和几何变换
 * Created by im_dsd on 2019-07-16
 */
public class CanvasDemo extends View {

    private int mW;
    private int mH;

    public CanvasDemo(Context context) {
        super(context);
    }

    public CanvasDemo(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasDemo(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CanvasDemo(Context context,  @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Path mPath;

    private Paint mPaint;

    {
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(40,40, 40, mPaint);
        // 将上面的操作保存起来
        canvas.save();
        mPaint.setColor(Color.BLUE);
        canvas.translate(mW / 2f, 0);
        canvas.rotate(90);
        canvas.drawCircle(0,40,40, mPaint);
        canvas.restore();
        mPaint.setColor(Color.YELLOW);
        canvas.drawCircle(40, mH / 2f, 40, mPaint);

        // 此处的 save restore 相当于指定 clipXXX 方法的作用范围，否则 clipXXX 后面的代码都会在裁剪区域执行
        canvas.save();
        canvas.clipRect(100,100,150,150);
        canvas.drawColor(Color.RED);
        canvas.restore();

        // 此方法裁剪出来的绘制区域会有毛边，所以不推荐用于裁剪圆形
//        canvas.clipPath()
    }
}
