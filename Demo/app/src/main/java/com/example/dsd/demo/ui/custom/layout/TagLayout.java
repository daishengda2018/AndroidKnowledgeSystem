package com.example.dsd.demo.ui.custom.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 Layout Demo
 *
 * Created by im_dsd on 2019-08-11
 */
public class TagLayout extends ViewGroup {

    private List<Rect> mChildRectList = new ArrayList<>();

    public TagLayout(Context context) {
        super(context);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int lineHeightUsed = 0;
        int lineWidthUsed = 0;
        int widthUsed = 0;
        int heightUsed = 0;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 测量子 View 尺寸。TagLayout 的子 view 是可以换行的，所以设置 widthUsed 参数为 0，让子 View 的尺寸不会受到挤压。
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            if (widthMode != MeasureSpec.UNSPECIFIED && lineWidthUsed + child.getMeasuredWidth() > widthSize) {
                // 需要换行了
                lineWidthUsed = 0;
                heightUsed += lineHeightUsed;
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            }
            Rect childBound;
            if (mChildRectList.size() >= i) {
                // 不存在则创建
                childBound = new Rect();
                mChildRectList.add(childBound);
            } else {
                childBound = mChildRectList.get(i);
            }
            // 存储 child 位置信息
            childBound.set(lineWidthUsed, heightUsed, lineWidthUsed + child.getMeasuredWidth(),
                            heightUsed + child.getMeasuredHeight());
            // 更新位置信息
            lineWidthUsed += child.getMeasuredWidth();
            // 获取一行中最大的尺寸
            lineHeightUsed = Math.max(lineHeightUsed, child.getMeasuredHeight());
            widthUsed = Math.max(lineWidthUsed, widthUsed);
        }

        // 使用的宽度和高度就是 TagLayout 的宽高啦
        heightUsed += lineHeightUsed;
        setMeasuredDimension(widthUsed, heightUsed);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 分析子 View 测量的过程
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    private void childMesareAnalasizy(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // TagLayout 已经使用过的空间，此处的计算是个难点
        int widthUseSize = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 难点1: 计算出对于每个子 View 尺寸的期望值
            // 1-1 获取开发者对于子 View 尺寸的设置
            LayoutParams layoutParams = child.getLayoutParams();
            int childWidthMode = 0;
            int childWidthSize = 0;
            // 1-2 获取父 View 具体的可用空间
            switch (layoutParams.width) {
                // 如果说子 View 被开发者设置为 match_parent
                case LayoutParams.MATCH_PARENT:
                    switch (widthMode) {
                        case MeasureSpec.EXACTLY:
                            // TagLayout 为 EXACTLY 模式下，子 View 可以填充的部位就是 TagLayout 的可用空间
                        case MeasureSpec.AT_MOST:
                            // TagLayout 为 AT_MOST 模式下有一个最大可用空间，子 View 要是想 match_parent 其实是和
                            // EXACTLY 模式一样的
                            childWidthMode = MeasureSpec.EXACTLY;
                            childWidthSize = widthSize - widthUseSize;
                            break;
                        case MeasureSpec.UNSPECIFIED:
                            // 当 TagLayout 为 UNSPECIFIED 不限制尺寸的时候，意味着可用空间无限大！空间无限大还想
                            // match_parent 二者完全是悖论，所以我们也要将子 View 的 mode 指定为 UNSPECIFIED
                            childWidthMode = MeasureSpec.UNSPECIFIED;
                            // 此时 size 已经没有作用了，写 0 就可以了
                            childWidthSize = 0;
                            break;
                    }
                case LayoutParams.WRAP_CONTENT:
                    break;
                default:
                    // 具体设置的尺寸
                    break;
            }
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mChildRectList.size() == 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (mChildRectList.size() <= i) {
                return;
            }
            View child = getChildAt(i);
            // 通过保存好的位置，设置子 View
            Rect rect = mChildRectList.get(i);
            child.layout(rect.left, rect.top, rect.right, rect.bottom);
        }
    }
}
