package com.example.dsd.demo.ui.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;


/**
 * 可以显示不规则图像的View
 * <p>
 * 可以通过shape属性指定希望裁剪的形状，
 * Created by im_dsd on 2019-06-09
 */
public class IrregularDrawableView extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint;
    private PorterDuffXfermode mDuffXfermode;
    private Bitmap mPathBitmap;
    private RectF mCute;
    private Bitmap mBitmap;
    private @DrawableRes int mDrawableRes;

    public IrregularDrawableView(Context context) {
        super(context);
        init();
    }

    public IrregularDrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IrregularDrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPathBitmap = getBitmap(mDrawableRes);
        mCute = new RectF(0, 0, getWidth(), getHeight());
        mBitmap = drawableToBitmap(getDrawable());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        if (mPathBitmap != null) {
            //离屏渲染，将图层保存起来
            int layer = canvas.saveLayer(mCute, mPaint, Canvas.ALL_SAVE_FLAG);
            canvas.drawBitmap(mPathBitmap, 0, 0, mPaint);
            mPaint.setXfermode(mDuffXfermode);
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
            // 复原状态
            mPaint.setXfermode(null);
            canvas.restoreToCount(layer);
        } else {
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        }
    }

    /**
     * 从资源文件中获取Bitmap
     *
     * @param resId
     * @return
     */
    private Bitmap getBitmap(@DrawableRes int resId) {
        if (resId == 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        options.inJustDecodeBounds = false;
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        // 计算采样率
        int inSampleSize = 1;
        if (srcHeight > getHeight() || srcWidth > getWidth()) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / getHeight());
            } else {
                inSampleSize = Math.round(srcWidth / getWidth());
            }
        }
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        Matrix matrix = getMatrix(bitmap);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        if (w > 0 && h > 0) {
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
            //对bitmap进行缩放
            Matrix matrix = getMatrix(bitmap);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return null;
    }

    private Matrix getMatrix(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        if (bitmap != null) {
            float scaleX = getWidth() * 1f / bitmap.getWidth();
            float scaleY = getHeight() * 1f / bitmap.getHeight();
            matrix.setScale(scaleX, scaleY);
        }
        return matrix;
    }

    /**
     * @param drawableRes
     */
    public void setMarkDrawableRes(@DrawableRes int drawableRes) {
        if (drawableRes != 0) {
            mPathBitmap = getBitmap(drawableRes);
        }
        mDrawableRes = drawableRes;
    }
}
