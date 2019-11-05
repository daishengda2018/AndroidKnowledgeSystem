package com.example.dsd.demo.ui.draw;

import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * 缩放助手
 * <p>
 * Created by im_dsd on 2019-11-03
 */
public class ScaleHelper {

    private float[] mScales;

    public ScaleHelper(float... scales) {
        mScales = scales;
    }

    /**
     * 更新平滑缩放比例，数组长度必须是偶数
     * 偶数索引表示要缩放的比例，奇数索引表示位置 (0~1)
     * 奇数索引必须要递增，即越往后的数值应越大
     * 例如：
     * [0.8, 0.5] 表示在50%处缩放到原来的80%
     * [0, 0, 1, 0.5, 0, 1]表示在起点处的比例是原来的0%，在50%处会恢复原样，到终点处会缩小到0%
     *
     * @param scales 每个位置上的缩放比例
     */
    void setupScale(float... scales) {
        // 如果没有指定缩放比例，默认不缩放
        if (scales.length == 0) {
            scales = new float[]{1, 0, 1, 1};
        }
        // 检查是否存在负数
        for (float tmp : scales) {
            if (tmp < 0) {
                throw new IllegalArgumentException("Array value can not be negative!");
            }
        }
        if (!Arrays.equals(mScales, scales)) {
            // 长度一定要为偶数
            if (scales.length < 2 || scales.length % 2 != 0) {
                throw new IllegalArgumentException("Array length no match!");
            }
            // 最后赋值
            mScales = scales;
            appendIfNeed();
            checkIsArrayLegal();
        }
    }

    /**
     * 获取指定位置的缩放比例
     * @param fraction 当前位置(0~1)
     */
    float getScale(float fraction) {
        float minScale = 1;
        float maxScale = 1;
        float scalePosition;
        float minFraction = 0, maxFraction = 1;
        // 顺序遍历，找到小于fraction的，最贴近的scale
        for (int i = 1; i < mScales.length; i += 2) {
            scalePosition = mScales[i];
            if (scalePosition <= fraction) {
                minScale = mScales[i - 1];
                minFraction = mScales[i];
            } else {
                break;
            }
        }
        // 倒序遍历，找到大于fraction的，最贴近的scale
        for (int i = mScales.length - 1; i >= 1; i -= 2) {
            scalePosition = mScales[i];
            if (scalePosition >= fraction) {
                maxScale = mScales[i - 1];
            } else {
                break;
            }
        }
        // 计算当前点fraction，在起始点minFraction与结束点maxFraction中的百分比
        fraction = solveTwoPointForm(minFraction, maxFraction, fraction);
        // 最大缩放 - 最小缩放 = 要缩放的范围
        float distance = maxScale - minScale;
        // 缩放范围 * 当前位置 = 当前缩放比例
        float scale = distance * fraction;
        // 加上基本的缩放比例
        float result = minScale + scale;
        // 如果得出的数值不合法，则直接返回基本缩放比例
        return isFinite(result) ? result : minScale;
    }

    /**
     * 将基于总长度的百分比转换成基于某个片段的百分比 (解两点式直线方程)
     *
     * @param startX   片段起始百分比
     * @param endX     片段结束百分比
     * @param currentX 总长度百分比
     * @return 该片段的百分比
     */
    private float solveTwoPointForm(float startX, float endX, float currentX) {
        return (currentX - startX) / (endX - startX);
    }

    /**
     * 判断数值是否合法
     *
     * @param value 要判断的数值
     * @return 合法为true，反之
     */
    private boolean isFinite(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value);
    }

    /**
     * 检查是否需要在两端追加元素
     */
    private void appendIfNeed() {
        if (mScales[1] != 0) {
            mScales = insertElement(true, mScales, 1, 0);
        }
        if (mScales[mScales.length - 1] != 1) {
            mScales = insertElement(false, mScales, 1, 1);
        }
    }

    /**
     * 检查数组是否合法
     */
    private void checkIsArrayLegal() {
        float min = mScales[1];
        float temp;
        for (int i = 1; i < mScales.length; i += 2) {
            temp = mScales[i];
            if (min > temp) {
                throw new IllegalArgumentException("Incorrect array value! position must be from small to large");
            } else {
                min = temp;
            }
        }
    }

    /**
     * 扩展数组元素
     *
     * @param isAddFromHead 是否从头部添加
     * @param target        目标数组
     * @param elements      需要插入的数值
     * @return 扩展后的数组
     */
    private float[] insertElement(boolean isAddFromHead, @NonNull float[] target, @NonNull float... elements) {
        float[] result = new float[target.length + elements.length];
        if (isAddFromHead) {
            System.arraycopy(elements, 0, result, 0, elements.length);
            System.arraycopy(target, 0, result, elements.length, target.length);
        } else {
            System.arraycopy(target, 0, result, 0, target.length);
            System.arraycopy(elements, 0, result, target.length, elements.length);
        }
        return result;
    }
}
