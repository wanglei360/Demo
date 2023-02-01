package com.ntrade.demo.tool

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.ntrade.demo.App

/** * 创建者：leiwu
 * * 时间：2022/11/11 14:25
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
object ColorUtils {

    fun getColor(colorId: Int): Int = ContextCompat.getColor(App.context, colorId)

    /**
     * 获取 startColor 颜色至 endColor 的过度色
     * @param animTime 动画时间，毫秒值
     * @param startColor 开始的颜色，可以用 Color.parseColor("#FF018786")、getColor(R.color.fff)
     * @param endColor 结束的颜色，可以用 Color.parseColor("#FF018786")、getColor(R.color.fff)
     */
    fun colorAnimator(
        animTime: Long,
        startColor: Int,
        endColor: Int,
        foo: ((Int) -> Unit)?
    ) {
        ObjectAnimator.ofObject(
            ArgbEvaluator(),
            startColor,
            endColor
        ).apply {
            duration = animTime
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                foo?.invoke(it.animatedValue.toString().toInt())
            }
        }.start()
    }

    /**
     * todo 有可能会有内存泄漏，没验证  也可以使用 ColorUtils.colorAnimator(Long,Int,Int,Unit) 方法
     * 给控件添加颜色的动画，如果 target = TextView,propertyName = backgroundColor,就是给TextView添加一个底色的颜色改变动画
     * @param target 动画的控件
     * @param propertyName backgroundColor、textColor
     * @param animTime 动画时间，毫秒值
     * @param startColor 开始的颜色，可以用 Color.parseColor("#FF018786")、getColor(R.color.fff)
     * @param endColor 结束的颜色
     */
    fun colorAnimator(
        target: View,
        propertyName: String,
        animTime: Long,
        startColor: Int,
        endColor: Int
    ) {
        ObjectAnimator.ofObject(
            target,
            propertyName,
            ArgbEvaluator(),
            startColor,
            endColor
        ).apply {
            duration = animTime
            interpolator = AccelerateDecelerateInterpolator()
        }.start()
    }

    /**
     * TODO 也可以使用 ArgbEvaluator().evaluate 方法，一样的效果，实现过程没看，估计应该差不多，
     * TODO android.animation 包下的，所以建议使用官方的
     * 获取两个颜色的过渡色
     * fraction值范围  0f-0.99f
     * 0返回的是startColor ,0.99返回的是endColor，0.01f-0.098f就是两个颜色的过渡色
     * @param fraction 值范围 0.01f-0.098f(1会变色)就是两个颜色的过渡色 , 0就是startColor ,0.99就是endColor，
     * @param startColor 开始的颜色
     * @param endColor  结束的颜色
     */
    fun getCurrentColor(fraction: Float, startColor: Int, endColor: Int): Int {
        val redCurrent: Int
        val blueCurrent: Int
        val greenCurrent: Int
        val alphaCurrent: Int
        val redStart = Color.red(startColor)
        val blueStart = Color.blue(startColor)
        val greenStart = Color.green(startColor)
        val alphaStart = Color.alpha(startColor)
        val redEnd = Color.red(endColor)
        val blueEnd = Color.blue(endColor)
        val greenEnd = Color.green(endColor)
        val alphaEnd = Color.alpha(endColor)
        val redDifference = redEnd - redStart
        val blueDifference = blueEnd - blueStart
        val greenDifference = greenEnd - greenStart
        val alphaDifference = alphaEnd - alphaStart
        redCurrent = (redStart + fraction * redDifference).toInt()
        blueCurrent = (blueStart + fraction * blueDifference).toInt()
        greenCurrent = (greenStart + fraction * greenDifference).toInt()
        alphaCurrent = (alphaStart + fraction * alphaDifference).toInt()
        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent)
    }
}