package com.example.dsd.demo.ui.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dsd.demo.R;
import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 头像绘制
 * Created by im_dsd on 2019-06-09
 */
public class AvatarView extends View {
    private Paint mPaint;
    private PorterDuffXfermode mDuffXfermode;
    private Bitmap mAvatar;
    private static final int WIDTH = (int)DisplayUtils.dp2px(100);
    private static final int PADDING = (int)DisplayUtils.dp2px(20);
    private RectF cute;


    public AvatarView(Context context) {
        super(context);
    }

    public AvatarView(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarView(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AvatarView(Context context,  @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Bitmap mPath;

    {
        mPaint = new Paint();
        mAvatar = getBitmap(R.drawable.timg, WIDTH);
        mPath = getBitmap(R.drawable.path, WIDTH);
        mDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cute = new RectF(PADDING, PADDING, PADDING + WIDTH, PADDING + WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //离屏渲染，将图层保存起来
        int layer = canvas.saveLayer(cute, mPaint);
//        canvas.drawOval(cute, mPaint);
        canvas.drawBitmap(mPath, PADDING, PADDING, mPaint);
        mPaint.setXfermode(mDuffXfermode);
        canvas.drawBitmap(mAvatar, PADDING, PADDING, mPaint);
        // 复原状态
        mPaint.setXfermode(null);
        canvas.restoreToCount(layer);
    }

    // 这段代码很重要，他涉及到一个性能优化的过程，先将Bitmap加载到内存中，获取相关信息（采样率）后显示。
    private Bitmap getBitmap(@DrawableRes int picId,  int width){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),picId, options);
        options.inJustDecodeBounds = false;
        options.inDensity = options.outWidth;
        options.inTargetDensity = width;
        return BitmapFactory.decodeResource(getResources(), picId, options);
    }
}
