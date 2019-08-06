package com.example.dsd.demo.ui.draw.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dsd.demo.R;
import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 文字围绕Image
 * <p>f
 * Created by im_dsd on 2019-07-14
 */
public class ImageTextView extends View {
    private Paint mPaint;
    private int mReqSize;
    private int mW;
    private int mH;
    private Bitmap mBitmap;
    private static String TEXT = "You can't speak English without asking questions! There are a few different question types in English. " +
        "In this session you'll see our presenters asking each other questions. We'll show you the grammar rules of question forms - " +
        "and then you'll have a chance to practise.";
    private float[] measureWidth = new float[1];
    private int mHeight;

    public ImageTextView(Context context) {
        super(context);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
    }

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(DisplayUtils.sp2px(18));
        mPaint.setColor(Color.BLACK);
        mReqSize = (int) (DisplayUtils.dp2px(50));
        mBitmap = getBitmap(R.drawable.timg, mReqSize, mReqSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, getWidth() - mBitmap.getWidth(), mBitmap.getHeight(), mPaint);
        int length = TEXT.length();
        int lines = 0;
        for (int start = 0, count = 0 ; start < length; start += count) {
            // 测量最多显示的文字个数。
            lines ++;
            int w = getWidth();
            float y = mPaint.getFontSpacing() * lines;
            if (y >= mBitmap.getHeight() && y <= mBitmap.getHeight() * 2) {
                w = getWidth() - mBitmap.getWidth();
            }
            // 关键API用户获取绘制数量
            count = mPaint.breakText(TEXT, start, length, true,w, measureWidth);
            canvas.drawText(TEXT, start, start + count, 0 , y, mPaint);
        }
    }

    /**
     * 获取Bitmap
     *
     * @param drawableRes 资源id
     * @param reqWidth    结果的宽度
     * @param reqHeight   结果的高度
     * @return 经过计算采样率的bitmap
     */
    Bitmap getBitmap(@DrawableRes int drawableRes, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只加载进入内存
        options.inJustDecodeBounds = true;
        // 获取option
        BitmapFactory.decodeResource(getResources(), drawableRes, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(), drawableRes, options);
    }

    /**
     * 计算采样率
     *
     * @param options   BitmapFactory.Options
     * @param reqWidth  结果的宽度
     * @param reqHeight 结果的高度
     * @return inSampleSize
     */
    int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
