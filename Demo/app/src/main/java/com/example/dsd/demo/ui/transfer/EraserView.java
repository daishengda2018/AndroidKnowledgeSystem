package com.example.dsd.demo.ui.transfer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.example.dsd.demo.R;

/**
 * 橡皮擦效果
 * <p>
 * Created by im_dsd on 2019-10-28
 */
public class EraserView extends View {
    private int mWidth;
    private int mHeight;
    private Path mPath;
    private Paint mPaint;
    private Bitmap mFgBitmap;
    private Bitmap mBgBitmap;
    private Canvas mCanvas;
    private float mPreX;
    private float mPreY;
    private int mScaledTouchSlop;

    public EraserView(Context context) {
        super(context);
    }

    public EraserView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EraserView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        cal();
        init();
    }

    /**
     * 计算参数
     */
    private void cal() {
        WindowManager wm = (WindowManager) getContext()
            .getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        mWidth = outMetrics.widthPixels;
        mHeight = outMetrics.heightPixels;
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    }

    /**
     * 初始化实例
     */
    private void init() {
        mPath = new Path();
        // 开启抗锯齿、抗抖动
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置路径结合处样式
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 设置笔触类型
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(50);
        // 生成前景图, config 必须有 alpha 通道
        mFgBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mFgBitmap);
        // 绘制画布背景为中性灰
        mCanvas.drawColor(0xFF808080);
        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        mBgBitmap = Bitmap.createScaledBitmap(mBgBitmap, mWidth, mHeight, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBgBitmap, 0, 0, null);
        canvas.drawBitmap(mFgBitmap, 0, 0, null);
        /*
         * 这里要注意canvas和mCanvas是两个不同的画布对象
         * 当我们在屏幕上移动手指绘制路径时会把路径通过mCanvas绘制到fgBitmap上
         * 每当我们手指移动一次均会将路径mPath作为目标图像绘制到mCanvas上，而在上面我们先在mCanvas上绘制了中性灰色
         * 两者会因为DST_IN模式的计算只显示中性灰，但是因为mPath的透明，计算生成的混合图像也会是透明的
         * 所以我们会得到“橡皮擦”的效果
         */
        mCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * 获取当前事件位置坐标
         */
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            // 手指接触屏幕重置路径
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(x, y);
                mPreX = x;
                mPreY = y;
                break;
            // 手指移动时连接路径
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mPreX);
                float dy = Math.abs(y - mPreY);
                if (dx >= mScaledTouchSlop || dy >= mScaledTouchSlop) {
                    // quadTo 可以让线段画的更平滑
                    mPath.quadTo(mPreX, mPreY, (x + mPreX) / 2, (y + mPreY) / 2);
                    mPreX = x;
                    mPreY = y;
                }
                break;
            default:
                break;
        }

        // 重绘视图
        invalidate();
        return true;
    }
}
