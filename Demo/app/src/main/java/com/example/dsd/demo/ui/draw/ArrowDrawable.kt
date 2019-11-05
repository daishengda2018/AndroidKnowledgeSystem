package com.example.dsd.demo.ui.draw

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

import com.example.dsd.demo.utils.DisplayUtils

/**
 * 自定义射箭动画
 *
 * Created by im_dsd on 2019-11-03
 */
class ArrowDrawable : Drawable() {
    private var mPaint: Paint? = null
    private val mBowLength = DisplayUtils.dp2px(200f)//弓长
    private val mCenterX: Double = 0.toDouble()

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun setAlpha(alpha: Int) {
        // 注意： setAlpha 和 getAlpha 必须成对出现，不然白写，没有意义
        mPaint!!.alpha = alpha
    }

    override fun getAlpha(): Int {
        // 注意：setAlpha 和 getAlpha 必须成对出现，不然白写，没有意义
        return mPaint!!.alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // 设置颜色过滤器
        mPaint!!.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        // 设置不透明度，这个方法返回的并不是具体的值，还是三种状态：不透明，半透明，全透明
        return PixelFormat.TRANSPARENT
    }

    override fun draw(canvas: Canvas) {
        // 自己想绘制的内容
    }

    internal fun getPointByAngle(angle: Float) {
        // 先把角度转成弧度
        val radian = angle * Math.PI / 180
        // 半径取弓长的一半
        val radius = (mBowLength / 2).toDouble()
        // x轴坐标值
        val x = (mCenterX + radius * Math.cos(radian)).toFloat()

    }
}
