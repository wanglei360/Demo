package com.ntrade.demo.view.progress_button

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.ntrade.demo.R
import com.ntrade.demo.tool.PaintUtils

/** * 创建者：leiwu
 * * 时间：2022/10/11 09:04
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class ProgressTest : View {


    private var mWidth = 0f
    private var mHeight = 0f
    private var textY = 0f
    private var textHeight = 0f
    private val str = "MyMyMy"
    private val paintUtils by lazy { PaintUtils() }
    private val mPath by lazy { Path() }
    private val mPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.background_d8d8d8)
            style = Paint.Style.FILL_AND_STROKE // 填充
            strokeWidth = 4f
            textSize = 200f
        }
    }
    private val testPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.color_red)
            style = Paint.Style.FILL_AND_STROKE // 填充
            strokeWidth = 2f
            textSize = 40f
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat() //控件的宽
        mHeight = h.toFloat() //控件的高
        textY = paintUtils.measureBaselineY(mPaint).toFloat()
        textHeight = paintUtils.measureHeight(mPaint).toFloat()
        invalidate()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.apply {

            mPath.moveTo(200f, 0f)
            mPath.lineTo(800f, 0f)
            mPath.lineTo(800f, mHeight)
            mPath.lineTo(200f, mHeight)
            mPath.close()
            drawPath(mPath, mPaint)

            mPaint.color = resources.getColor(R.color.color_blue)

            save()
            mPath.reset()
            mPath.moveTo(250f, 50f)
            mPath.lineTo(700f, 50f)
            mPath.lineTo(700f, mHeight - 50f)
            mPath.lineTo(250f, mHeight - 50f)
            mPath.close()
            clipPath(mPath)//抠出一个单独的绘制区域

            mPaint.color = resources.getColor(R.color.color_green1)
            val ddd = paintUtils.measureTextHalfHeight(mPaint)
            // 在抠出的区域中写字，坐标系还是原来的坐标，但只在扣出来的区域绘制
            drawText(
                str,
                150f,
                mHeight / 2 + ddd,
                mPaint
            )

            restore()
            test()


            mPaint.textSize = 100f
            drawText(str, 150f, mHeight - paintUtils.measureBaselineToBottomHeight(mPaint), mPaint)

        }
    }

    private fun Canvas.drawMPath(num: Int, color: Int) {
        mPath.reset()
        testPaint.color = color
        val startX = mWidth - mWidth / 3 + 20
        var startY = getNum(4)
        var viewHeight = getNum(30)
        when (num) {
            0 -> {
                mPath.moveTo(startX, startY)
                mPath.lineTo(startX + viewHeight, startY)
                mPath.lineTo(startX + viewHeight, viewHeight + startY)
                mPath.lineTo(startX, viewHeight + startY)
                mPath.close()
                drawPath(mPath, mPaint)
                drawText(
                    "Top",
                    startX + viewHeight + 20f,
                    (startY + getNum(24)) - paintUtils.measureBaselineToBottomHeight(testPaint),
                    testPaint
                )
            }
            1 -> {
                startY = startY * 2 + viewHeight * 1
                mPath.moveTo(startX, startY)
                mPath.lineTo(startX + viewHeight, startY)
                mPath.lineTo(startX + viewHeight, viewHeight + startY)
                mPath.lineTo(startX, viewHeight + startY)
                mPath.close()
                drawPath(mPath, mPaint)
                drawText(
                    "Ascent",
                    startX + viewHeight + 20f,
                    (startY + getNum(24)) - paintUtils.measureBaselineToBottomHeight(testPaint),
                    testPaint
                )
            }
            2 -> {
                startY = startY * 3 + viewHeight * 2
                mPath.moveTo(startX, startY)
                mPath.lineTo(startX + viewHeight, startY)
                mPath.lineTo(startX + viewHeight, viewHeight + startY)
                mPath.lineTo(startX, viewHeight + startY)
                mPath.close()
                drawPath(mPath, mPaint)
                drawText(
                    "Baseline",
                    startX + viewHeight + 20f,
                    (startY + getNum(24)) - paintUtils.measureBaselineToBottomHeight(testPaint),
                    testPaint
                )
            }
            3 -> {
                startY = startY * 4 + viewHeight * 3
                mPath.moveTo(startX, startY)
                mPath.lineTo(startX + viewHeight, startY)
                mPath.lineTo(startX + viewHeight, viewHeight + startY)
                mPath.lineTo(startX, viewHeight + startY)
                mPath.close()
                drawPath(mPath, mPaint)
                drawText(
                    "Descent",
                    startX + viewHeight + 20f,
                    (startY + getNum(24)) - paintUtils.measureBaselineToBottomHeight(testPaint),
                    testPaint
                )
            }
            else -> {
                startY = startY * 5 + viewHeight * 4
                mPath.moveTo(startX, startY)
                mPath.lineTo(startX + viewHeight, startY)
                mPath.lineTo(startX + viewHeight, viewHeight + startY)
                mPath.lineTo(startX, viewHeight + startY)
                mPath.close()
                drawPath(mPath, mPaint)
                drawText(
                    "Bottom",
                    startX + viewHeight + 20f,
                    (startY + getNum(24)) - paintUtils.measureBaselineToBottomHeight(testPaint),
                    testPaint
                )
            }
        }
    }

    private fun Canvas.test() {
        val fm = mPaint.fontMetricsInt
        //TODO 下面这些值是为了图看起来更方便理解，所以适当的改变了值
        //TODO 详细说明：https://www.jianshu.com/p/8b97627b21c4
        val baseline = paintUtils.measureTextHalfHeight(mPaint) + getNum(2)
        val top = fm.top + baseline + getNum(7)
        val ascent = fm.ascent + baseline + getNum(12)
        val descent = fm.descent + baseline - getNum(2)
        val bottom = fm.bottom + baseline + getNum(4)
        mPaint.color = resources.getColor(R.color.purple_200)
        drawMPath(0, mPaint.color)
        drawLine(
            0f,
            mHeight / 2 + top,
            mWidth - mWidth / 3,
            mHeight / 2 + top,
            mPaint
        )
        mPaint.color = resources.getColor(R.color.teal_200)
        drawMPath(1, mPaint.color)
        drawLine(
            0f,
            mHeight / 2 + ascent,
            mWidth - mWidth / 3,
            mHeight / 2 + ascent,
            mPaint
        )
        mPaint.color = resources.getColor(R.color.background_red)
        drawMPath(2, mPaint.color)
        drawLine(
            0f,
            mHeight / 2 + baseline,
            mWidth - mWidth / 3,
            mHeight / 2 + baseline,
            mPaint
        )
        mPaint.color = resources.getColor(R.color.color_blue)
        drawMPath(3, mPaint.color)
        drawLine(
            0f,
            mHeight / 2 + descent,
            mWidth - mWidth / 3,
            mHeight / 2 + descent,
            mPaint
        )
        mPaint.color = resources.getColor(R.color.text_FFA500)
        drawMPath(4, mPaint.color)
        drawLine(
            0f,
            mHeight / 2 + bottom,
            mWidth - mWidth / 3,
            mHeight / 2 + bottom,
            mPaint
        )
    }

    private fun getNum(num: Int): Float = mWidth / 360f * num
}