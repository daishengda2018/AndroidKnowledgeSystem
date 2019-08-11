package com.example.dsd.demo.ui.custom.text;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.example.dsd.demo.R;
import com.example.dsd.demo.utils.DisplayUtils;

/**
 * 简单模仿 https://github.com/rengwuxian/MaterialEditText
 *
 * Created by im_dsd on 2019-08-07
 */
public class MaterialEditText extends android.support.v7.widget.AppCompatEditText {

    /**
     * 字体大小
     */
    public static final float TEXT_SIZE = DisplayUtils.sp2px(12);
    public static final float TEXT_MARGE = DisplayUtils.dp2px(8);
    private static final float VERTICAL_OFFSET = DisplayUtils.dp2px(38);
    private static final float VERTICAL_OFFSET_EXTRA = DisplayUtils.dp2px(16);
    private Rect mPadding;
    private Paint mPaint;
    private boolean isShow;
    private float mFloatingLabelFraction;

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        typedArray.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#333333"));
        mPaint.setTextSize(TEXT_SIZE);

        mPadding = new Rect();
        // 通过背景拿到的 padding 才是最终的初始值
        getBackground().getPadding(mPadding);
        setPadding(mPadding.left, (int) (mPadding.top + TEXT_SIZE + TEXT_MARGE), mPadding.right, mPadding.bottom);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 每次输入的文字的改变都会引起 onDraw 的执行
                String content = s.toString();
                if (isShow && TextUtils.isEmpty(content)) {
                    // 隐藏
                    isShow = false;
                    getAnimator().reverse();
                } else if (!isShow && !TextUtils.isEmpty(content)) {
                    // 显示
                    isShow = true;
                    getAnimator().start();
                }
            }
        });
    }

    private ObjectAnimator getAnimator() {
        // 自定义属性动画，需要注意的点就是需要声明 getXXX、setXXX 方法，而且必须是 public
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0, 1);
        animator.setDuration(300);
        return animator;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置透明度
        mPaint.setAlpha((int) (mFloatingLabelFraction * 0xff));
        canvas.drawText(getHint().toString(), mPadding.left, VERTICAL_OFFSET - mFloatingLabelFraction * VERTICAL_OFFSET_EXTRA, mPaint);
    }


    public void setFloatingLabelFraction(float value) {
        mFloatingLabelFraction = value;
        // 通知重新绘制
        invalidate();
    }

    public float getFloatingLabelFraction() {
        return mFloatingLabelFraction;
    }
}
