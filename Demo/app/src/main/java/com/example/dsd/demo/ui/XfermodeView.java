package com.example.dsd.demo.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Xfermode demo
 * <p>
 * Created by im_dsd on 2019-10-20
 */
public class XfermodeView extends View {

    /**
     * PorterDuff模式常量 可以在此更改不同的模式测试
     */
    private static final PorterDuff.Mode MODE = PorterDuff.Mode.CLEAR;
    private PorterDuffXfermode mPorterDuffXfermode;
    /**
     * 屏幕宽高
     */
    private int mScreenW, mScreenH;
    private Bitmap mSrcBitmap, mDstBitmap;
    /**
     * 源图和目标图宽高
     */
    private int width = 120;
    private int height = 120;
    private Paint mPaint;

    public XfermodeView(Context context) {
        this(context, null);
    }

    public XfermodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenW = outMetrics.widthPixels;
        mScreenH = outMetrics.heightPixels;
        //创建一个 PorterDuffXfermode 对象
        mPorterDuffXfermode = new PorterDuffXfermode(MODE);
        //创建原图和目标图
        mSrcBitmap = makeSrc(width, height);
        mDstBitmap = makeDst(width, height);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * 创建一个圆形bitmap，作为dst图
     */
    private Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFFFFCC44);
        c.drawOval(new RectF(0, 0, w * 3 / 4, h * 3 / 4), p);
        return bm;
    }

    /**
     * 创建一个矩形bitmap，作为src图
     */
    private Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF66AAFF);
        c.drawRect(w / 3, h / 3, w * 19 / 20, h * 19 / 20, p);
        return bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        // 绘制 “src” 蓝色矩形原图
        canvas.drawBitmap(mSrcBitmap, mScreenW / 8 - width / 4, mScreenH / 12 - height / 4, mPaint);
        // 绘制 “dst” 黄色圆形原图
        canvas.drawBitmap(mDstBitmap, mScreenW / 2, mScreenH / 12, mPaint);

        // 创建一个图层，在图层上演示图形混合后的效果
        int sc = canvas.saveLayer(0, 0, mScreenW, mScreenH, mPaint);

        // 先绘制“dst”黄色圆形
        canvas.drawBitmap(mDstBitmap, mScreenW / 4, mScreenH / 3, mPaint);
        // 设置 Paint 的 Xfermode
        mPaint.setXfermode(mPorterDuffXfermode);
        canvas.drawBitmap(mSrcBitmap, mScreenW / 4, mScreenH / 3, mPaint);
        mPaint.setXfermode(null);
        // 还原画布
        canvas.restoreToCount(sc);
    }
}