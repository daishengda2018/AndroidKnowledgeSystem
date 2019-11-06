package com.example.dsd.demo.ui.transfer.wechat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dsd.demo.R;
import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 微信 tag view
 * <p>
 * Created by im_dsd on 2019-11-06
 */
public class WeChatTagView extends View {

    private String mText;
    private int mTextSize;
    private int mColor;
    private BitmapDrawable mIcon;
    private RectF mIconRect;
    private Paint mTextPaint;
    private Rect mTextBound = new Rect();
    private float mAlpha = 0;
    private Paint mPaint;
    private PorterDuffXfermode mXfermode;

    public WeChatTagView(Context context) {
        this(context, null);
    }

    public WeChatTagView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeChatTagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WeChatTagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WeChatTagView);
        mText = array.getString(R.styleable.WeChatTagView_text);
        mTextSize = array.getDimensionPixelSize(R.styleable.WeChatTagView_textSize, 10);
        mColor = array.getColor(R.styleable.WeChatTagView_color, Color.BLACK);
        mIcon = (BitmapDrawable)(array.getDrawable(R.styleable.WeChatTagView_icon));
        array.recycle();

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff555555);
        // 得到text绘制范围
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) DisplayUtils.dp2px(20);
        int width = resolveSize(size, widthMeasureSpec);
        int height = resolveSize(size, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 得到绘制icon的宽
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        // 控件的高度-文本的高度-内边距
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height();
        int bitmapSize = Math.min(width, height);
        int left = getMeasuredWidth() / 2 - bitmapSize / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapSize / 2;
        // 设置icon的绘制范围
        mIconRect = new RectF(left, top, left + bitmapSize, top + bitmapSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int alpha = (int) Math.ceil((255 * mAlpha));
        // 绘制颜色
        mPaint.setAlpha(alpha);
        int layer = canvas.saveLayer(mIconRect, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mIcon.getBitmap(), null, mIconRect, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layer);

        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);
    }

    private void drawSourceText(Canvas canvas, int alpha)
    {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff333333);
        mTextPaint.setAlpha(255 - alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2 - mTextBound.width() / 2,
            mIconRect.bottom + mTextBound.height(), mTextPaint);
    }

    private void drawTargetText(Canvas canvas, int alpha)
    {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2 - mTextBound.width() / 2,
            mIconRect.bottom + mTextBound.height(), mTextPaint);

    }

    /**
     * 设置透明度
     * @param alpha 0 ~ 1 的取值范围
     */
    public void setIconAlpha(float alpha) {
        mAlpha = alpha;
        invalidate();
    }
}
