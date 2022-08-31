package com.ntrade.demo.view.chart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import com.ntrade.demo.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** * 创建者：leiwu
 * * 时间：2022/8/29 15:03
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class PancakeView : View {

    private var isShowSplitLine = true
    private var isStartAnim = true

    private var isAniming = false
    private val radius = 200f//半径
    private var centerX = -1f
    private var centerY = -1f
    private var peripheralWidth = 30f//点击之后突出的那一块的宽度
    private var oldSelPosition = -1
    private var mHeight = 0f//View的高
    private var mWidth = 0f//View的宽
    private var splitLineWidth = 10f//分割线的宽度
    private var mAnimValue = 0f

    private val mDatas = ArrayList<PancakeData>()
    private var angles: FloatArray? = null
    private val mPath = Path()

    private val spotPaint by lazy {
        getPaint(getColor(R.color.bessel_text), 1, Paint.Style.FILL_AND_STROKE)
    }
    private val linePaint by lazy {
        getPaint(getColor(R.color.text_000000), 5, Paint.Style.FILL_AND_STROKE)
    }
    private val lasdfinePaint by lazy {
        getPaint(getColor(R.color.text_cc0000), 5, Paint.Style.FILL_AND_STROKE)
    }
    private val splitLinePaint by lazy {
        getPaint(
            getColor(R.color.color_caae7e),
            splitLineWidth.toInt(),
            Paint.Style.FILL_AND_STROKE
        )
    }

    private val textPaint by lazy {
        getPaint(
            getColor(R.color.text_000000),
            1,
            Paint.Style.FILL_AND_STROKE
        ).apply {
            textSize = 40f
        }
    }

    private val animator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                mAnimValue = animation.animatedValue.toString().toFloat()
                postInvalidate()
            }
            addListener(MyChartView.MAnimatorListener {
                isAniming = it
            })
            duration = 600
        }
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        initAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
//        initAttrs(attrs)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()//控件的宽
        mHeight = h.toFloat()//控件的高  TSW19740324
        centerX = mWidth / 3
        centerY = mHeight / 2
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                if (isAniming) return super.onTouchEvent(event) //动画进行时，不能点击
                setClik(event)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mDatas.isEmpty()) return
        canvas?.apply {
            if (isStartAnim) drawAnim()
            else drawNoAnim()
        }
    }

    private val splitLineDegrees by lazy { ArrayList<Float>() }
    private fun Canvas.drawAnim() {
        var startDegree = 0f
        var endDegree = 0f
        splitLineDegrees.clear()
        mDatas.forEach {
            startDegree += endDegree
            endDegree = it.endDegree * mAnimValue
            drawArc(
                it.oval,
                startDegree,  //开始角度
                endDegree,  //扫过的角度
                true,  //是否使用中心
                it.mPaint
            )
            splitLineDegrees.add(startDegree)
        }
        if (isShowSplitLine) {
            splitLinePaint.strokeWidth = splitLineWidth * mAnimValue
            for (x in 0 until mDatas.size) {
                calculatePosition(splitLineDegrees[x] + 90, radius + peripheralWidth).apply {
                    drawLine(centerX, centerY, this[0], this[1], splitLinePaint)
                }
            }
        }


        drawArc(
            RectF(
                (centerX - 62),
                (centerY - 62),
                (centerX + 62),
                (centerY + 62)
            ),
            0f,  //开始角度
            360f,  //扫过的角度
            true,  //是否使用中心
            splitLinePaint
        )

        if (mAnimValue == 1f) {
            mDatas.forEach {
                drawText(it.num, it.textX, it.textY, textPaint)
            }
        }
    }

    private fun Canvas.drawNoAnim() {
        mDatas.forEach {
            drawArc(
                it.oval,
                it.startDegree,  //开始角度
                it.endDegree,  //扫过的角度
                true,  //是否使用中心
                it.mPaint
            )

            drawLine(
                it.startX,
                it.startY,
                it.endX,
                it.endY,
                linePaint
            )

            drawLine(
                it.textLineStartX,
                it.textLineStartY,
                it.textLineEndX,
                it.textLineEndY,
                linePaint
            )

            drawText(it.num, it.textX, it.textY - 6, textPaint)
        }
        if (isShowSplitLine) {
            mDatas.forEach {
                calculatePosition(it.startDegree + 90, radius + peripheralWidth).apply {
                    drawLine(centerX, centerY, this[0], this[1], splitLinePaint)
                }
            }
        }
    }

    private fun Canvas.drawSpot(degree: Float) {
        calculatePosition(degree, radius - 40).apply {
            val startX = this[0]
            val startY = this[1]
            drawArc(
                RectF(
                    (startX - 12),
                    (startY - 12),
                    (startX + 12),
                    (startY + 12)
                ),
                0f,  //开始角度
                360f,  //扫过的角度
                true,  //是否使用中心
                getRandomColorPaint()
            )
        }
    }

    /**
     * 1：通过atan2函数计算出点击时的角度
     * 2：通过象限转换成坐标系的角度
     */
    private fun setClik(event: MotionEvent) {
        val eventX = event.x
        val eventY = event.y
        if (eventX > centerX + radius || eventX < centerX - radius
            || eventY > centerY + radius || eventY < centerY - radius
        ) return
        val x: Float = eventX - centerX
        val y: Float = -eventY - -centerY
        val atan = atan2(y.toDouble(), x.toDouble())
        var touchAngle = Math.toDegrees(atan).toFloat()
        //当前弧线 起始点 相对于 横轴 的夹角度数,由于扇形的绘制是从三点钟方向开始计为0度，所以需要下面的转换
        if (x > 0 && y > 0) { //1象限
            touchAngle = 360 - touchAngle
        } else if (x < 0 && y > 0) { //2象限
            touchAngle = 360 - touchAngle
        } else if (x < 0 && y < 0) { //3象限
            touchAngle = abs(touchAngle)
        } else if (x > 0 && y < 0) { //4象限
            touchAngle = abs(touchAngle)
        }
        var selPosition = -1
        angles?.apply {
            for (z in 0 until size) {
                if (touchAngle <= this[z]) {
                    selPosition = z
                    break
                }
            }
        }
        if (selPosition != -1) {
            if (oldSelPosition != -1) {
                mDatas[oldSelPosition].apply {
                    oval = RectF(
                        centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius
                    )
                }
            }
            if (selPosition == oldSelPosition) {
                mDatas[oldSelPosition].apply {
                    oval = RectF(
                        centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius
                    )
                }
                oldSelPosition = -1
            } else {
                mDatas[selPosition].apply {
                    oval = RectF(
                        centerX - (radius + peripheralWidth),
                        centerY - (radius + peripheralWidth),
                        centerX + (radius + peripheralWidth),
                        centerY + (radius + peripheralWidth)
                    )
                }
                oldSelPosition = selPosition
            }
            postInvalidate()
        }
    }

    /**
     * 获取指定度数在外弧上的坐标
     * @param degree 旋转的度数
     * @param radius 半径
     */
    private fun calculatePosition(degree: Float, radius: Float): FloatArray {
        //由于Math.sin(double a)中参数a不是度数而是弧度，所以需要将度数转化为弧度
        //而Math.toRadians(degree)的作用就是将度数转化为弧度
        //扇形弧线中心点距离圆心的x坐标
        //sin 一二正，三四负 sin（180-a）=sin(a)
        val x = (sin(Math.toRadians(degree.toDouble())) * radius).toFloat()
        //扇形弧线中心点距离圆心的y坐标
        //cos 一四正，二三负
        val y = (cos(Math.toRadians(degree.toDouble())) * radius).toFloat()

        //每段弧度的中心坐标(扇形弧线中心点相对于view的坐标)
        val startX = centerX + x
        val startY = centerY - y
        val position = FloatArray(2)
        position[0] = startX
        position[1] = startY
        return position
    }


    fun setDatas(datas: ArrayList<Int>) {
        mDatas.clear()
        oldSelPosition = -1
        angles = FloatArray(datas.size)
        var angle = 0f
        for (x in 0 until datas.size) {
            angle += 360f / 100f * datas[x]
            angles!![x] = angle
        }
        if (isStartAnim) animStyle(datas)
        else noAnimStyle(datas)
    }

    private fun animStyle(datas: ArrayList<Int>) {
        var degree = 0f
        var position = 0
        datas.forEach {
            val newDegree = 360f / 100f * it
            val degreeTotal = degree + newDegree
            val coordinate =
                calculatePosition(degreeTotal + 90 - newDegree / 2, radius * 0.76f)

            val textX = coordinate[0] - textPaint.measureText("$it") / 2
            val textY = coordinate[1] + measureHeight(textPaint) / 2
            mDatas.add(
                PancakeData(
                    num = "$it",// 100中的几份
                    startDegree = degree,// 360度中开始的度
                    endDegree = newDegree,// 360度中结束的度
                    startX = -1f,// 斜线线段的起始x坐标 todo 没用，所以没有赋值
                    startY = -1f,// 斜线线段的起始y坐标 todo 没用，所以没有赋值
                    endX = -1f,// 斜线线段的结束x坐标 todo 没用，所以没有赋值
                    endY = -1f,// 斜线线段的结束y坐标 todo 没用，所以没有赋值
                    textLineStartX = -1f,
                    textLineStartY = -1f,
                    textLineEndX = -1f,
                    textLineEndY = -1f,
                    textX = textX,// 文本的起始x坐标
                    textY = textY,// 文本的起始Y坐标
                    mPaint = getPaint(randomColor(), 1, Paint.Style.FILL_AND_STROKE),
                    position = position++,
                    oval = RectF(
                        centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius
                    )
                )
            )
            degree += newDegree
        }
        animator.start()
    }

    private fun noAnimStyle(datas: ArrayList<Int>) {
        var degree = 0f
        var position = 0
        datas.forEach {
            val newDegree = 360f / 100f * it
            val degreeTotal = degree + newDegree
            val startCoordinate0 =
                calculatePosition(degreeTotal + 90 - newDegree / 2, radius - peripheralWidth)
            val endCoordinate1 =
                calculatePosition(degreeTotal + 90 - newDegree / 2, radius + peripheralWidth)

            val lineX = endCoordinate1[0]
            val lineY = endCoordinate1[1]
            val textWidth = textPaint.measureText("$it")
            val textHeight = measureHeight(textPaint)
            var textX = 0f
            val textY = endCoordinate1[1] + textHeight / 2
            val textLineEndX =
                if (lineX > centerX) {
                    textX = endCoordinate1[0] + textWidth
                    endCoordinate1[0] + textWidth
                } else {
                    textX = endCoordinate1[0] - textWidth * 2
                    endCoordinate1[0] - textWidth
                }
            mDatas.add(
                PancakeData(
                    num = "$it",// 100中的几份
                    startDegree = degree,// 360度中开始的度
                    endDegree = newDegree,// 360度中结束的度
                    startX = startCoordinate0[0],// 斜线线段的起始x坐标
                    startY = startCoordinate0[1],// 斜线线段的起始y坐标
                    endX = endCoordinate1[0],// 斜线线段的结束x坐标
                    endY = endCoordinate1[1],// 斜线线段的结束y坐标
                    textLineStartX = endCoordinate1[0],
                    textLineStartY = endCoordinate1[1],
                    textLineEndX = textLineEndX,
                    textLineEndY = endCoordinate1[1],
                    textX = textX,// 文本的起始x坐标
                    textY = textY,// 文本的起始Y坐标
                    mPaint = getPaint(randomColor(), 1, Paint.Style.FILL_AND_STROKE),
                    position = position++,
                    oval = RectF(
                        centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius
                    )
                )
            )
            degree += newDegree
        }
        postInvalidate()
    }


    private fun getColor(id: Int): Int = context.resources.getColor(id)

    /**
     * 获取要画的文本的高度
     * todo 获取宽度可以直接用 TextView 的 Paint 调用measureText("字符串")
     * todo textView.paint.measureText("字符串");
     */
    private fun measureHeight(paint: Paint): Int {
        val fm = paint.fontMetricsInt
        return fm.top.inv() - (fm.top.inv() - fm.ascent.inv()) - (fm.bottom - fm.descent)
    }

    /**
     * @param color 线的颜色
     * @param width 线的宽度
     * @param style
     *
     *Paint.Style.STROKE    只绘制图形轮廓（描边）
     *
     * Paint.Style.FILL      只绘制图形内容 (填充)
     *
     * Paint.Style.FILL_AND_STROKE 既绘制轮廓也绘制内容
     * @return Paint
     */
    private fun getPaint(color: Int, width: Int, style: Paint.Style): Paint {
        val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = color
        mPaint.strokeWidth = width.toFloat()//设置画笔粗细
        mPaint.style = style
        mPaint.isAntiAlias = true// 设置画笔的锯齿效果
        return mPaint
    }

    private fun log(str: String) {
        Log.d("MYCHARTVIEW1", str)
    }

    private fun getRandomColorPaint(): Paint {
        spotPaint.color = randomColor()
        return spotPaint
    }

    /**
     * 生成随机颜色
     */
    private fun randomColor(): Int {
        return Random().let {
            Color.rgb(
                it.nextInt(255),
                it.nextInt(255),
                it.nextInt(255)
            )
        }
    }
}

data class PancakeData(
    val num: String,// 100中的几份
    val startDegree: Float,// 360度中开始的度
    val endDegree: Float,// 360度中结束的度
    val startX: Float,// 斜线线段的起始x坐标
    val startY: Float,// 斜线线段的起始y坐标
    val endX: Float,// 斜线线段的结束x坐标
    val endY: Float,// 斜线线段的结束y坐标
    val textLineStartX: Float,//
    val textLineStartY: Float,//
    val textLineEndX: Float,//
    val textLineEndY: Float,//
    val textX: Float,// 文本的起始x坐标
    val textY: Float,// 文本的起始Y坐标
    val mPaint: Paint,//
    val position: Int,//
    var oval: RectF,//
)