package com.ntrade.demo.tool

import android.graphics.Paint

/**  创建者：leiwu
 * <p> 时间：2022/10/11 11:00
 * <p> 类描述：
 * <p> 修改人：
 * <p> 修改时间：
 * <p> 备注：drawText 方法画文字的时候，是在基线 Baseline 作为 y 点开始画的
 * 解释：https://www.jianshu.com/p/8b97627b21c4
 */
class PaintUtils {

    /**
     * 获取要画的文本的高度
     * todo 获取宽度可以直接用 TextView 的 Paint 调用measureText("字符串")
     * todo textView.paint.measureText("字符串");
     */
    fun measureHeight(paint: Paint): Int =
        paint.fontMetricsInt.let {
            it.top.inv() - (it.top.inv() - it.ascent.inv()) - (it.bottom - it.descent)
        }

    /**
     * TODO 文字与底部对齐
     * 计算基线到底的距离
     * 因为基线的问题，所以直接使用 measureHeight 的方式获取的不准确
     * 用view的 高度 - 当前方法的值 = drawText 方法中的 y 的值 ，可以使文字与底部对齐
     * 解释：https://www.jianshu.com/p/8b97627b21c4
     */
    fun measureBaselineToBottomHeight(paint: Paint): Int =
        paint.fontMetricsInt.let {
            it.bottom - (it.top.inv() - it.ascent.inv())
            it.bottom
        }

    /**
     * TODO 与顶部对齐
     * 计算基线到顶部的距离
     * 因为基线的问题，所以直接使用 measureHeight 的方式获取的不准确
     * 使用当前方法获取的值给 drawText 方法中参数 y 赋值，可以使文字与顶部对齐
     * 解释：https://www.jianshu.com/p/8b97627b21c4
     */
    fun measureBaselineY(paint: Paint): Int =
        paint.fontMetricsInt.let {
            it.ascent.inv() - (it.top.inv() - it.ascent.inv())
        }

    /**
     * 计算文本一半的高度(是Baseline该往下移动多少才能让字在中间显示)
     * todo 因为基线的问题，所以直接使用 measureHeight/2 的方式获取的不准确
     * 解释：https://www.jianshu.com/p/8b97627b21c4
     */
    fun measureTextHalfHeight(paint: Paint): Int =
        paint.fontMetricsInt.let {
            (it.ascent.inv() - it.descent) / 2
        }
}