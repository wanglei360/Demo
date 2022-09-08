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
    private var isShowSmallCircular = true
    private var isStartAnim = true
    private var mBackgroundColor = Color.parseColor("#ffffff")
    private var mTextColor = Color.parseColor("#000000")
    private var mLineColor = Color.parseColor("#000000")
    private var animatorDuration = 600

    private var isAniming = false
    private var radius = 0f//半径
    private var centerX = -1f
    private var centerY = -1f
    private var peripheralWidth = 1f//点击之后突出的那一块的宽度
    private var oldSelPosition = -1
    private var mHeight = 0f//View的高
    private var mWidth = 0f//View的宽
    private var splitLineWidth = 10f//分割线的宽度
    private var mAnimValue = 0f

    private val mDatas by lazy { ArrayList<PancakeData>() }
    private val splitLineDegrees by lazy { ArrayList<Float>() }
    private var angles: FloatArray? = null

    private val linePaint by lazy {
        getPaint(mLineColor, 5, Paint.Style.FILL_AND_STROKE)
    }
    private val splitLinePaint by lazy {//分割线
        getPaint(
            mBackgroundColor,
            splitLineWidth.toInt(),
            Paint.Style.FILL_AND_STROKE
        )
    }

    private val smallCircularPaint by lazy {//中间小圆
        getPaint(
            mBackgroundColor,
            splitLineWidth.toInt(),
            Paint.Style.FILL_AND_STROKE
        )
    }

    private val textPaint by lazy {
        getPaint(
            mTextColor,
            1,
            Paint.Style.FILL_AND_STROKE
        )
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
            duration = animatorDuration.toLong()
        }
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.pancake).apply {
            mBackgroundColor = getColor(
                R.styleable.pancake_pancake_background_color,
                Color.parseColor("#ffffff")
            )
            setBackgroundColor(mBackgroundColor)
            splitLinePaint.color = mBackgroundColor
            smallCircularPaint.color = mBackgroundColor

            mTextColor = getColor(
                R.styleable.pancake_pancake_text_color,
                Color.parseColor("#000000")
            )
            textPaint.color = mTextColor

            mLineColor = getColor(
                R.styleable.pancake_pancake_line_color,
                Color.parseColor("#000000")
            )
            linePaint.color = mLineColor

            isShowSplitLine = getBoolean(R.styleable.pancake_pancake_is_show_split_line, true)
            isStartAnim = getBoolean(R.styleable.pancake_pancake_is_start_anim, true)
            isShowSmallCircular =
                getBoolean(R.styleable.pancake_pancake_is_show_small_circular, true)

            animatorDuration = getInt(R.styleable.pancake_pancake_animator_duration, 500)
            animator.duration = animatorDuration.toLong()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()//控件的宽
        mHeight = h.toFloat()//控件的高  TSW19740324
        centerX = mWidth / 3
        centerY = mHeight / 2
        radius = mWidth / 5
        peripheralWidth = mWidth / 27f
        textPaint.textSize = mWidth / 27f
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

    private fun Canvas.drawAnim() {
        var startDegree = 0f
        var endDegree = 0f
        splitLineDegrees.clear()
        mDatas.forEach {
            startDegree += endDegree
            endDegree = it.endDegree * mAnimValue
            drawArc(
                it.oval!!,
                startDegree,  //开始角度
                endDegree,  //扫过的角度
                true,  //是否使用中心
                it.mPaint!!
            )
            splitLineDegrees.add(startDegree)
        }
        if (isShowSplitLine) {
            splitLinePaint.strokeWidth = splitLineWidth * mAnimValue
            for (x in 0 until mDatas.size) {
                calculatePosition(splitLineDegrees[x] + 90, radius + peripheralWidth * 0.4f).apply {
                    drawLine(centerX, centerY, this[0], this[1], splitLinePaint)
                }
            }
        }

        if (isShowSmallCircular)
            drawArc(//中心小圆
                RectF(
                    (centerX - radius / 3),
                    (centerY - radius / 3),
                    (centerX + radius / 3),
                    (centerY + radius / 3)
                ),
                0f,  //开始角度
                360f,  //扫过的角度
                true,  //是否使用中心
                smallCircularPaint
            )

        if (mAnimValue == 1f) {
            mDatas.forEach {
                drawText(it.text, it.textX, it.textY, textPaint)
            }
        }
    }

    private fun Canvas.drawNoAnim() {
        mDatas.forEach {
            drawArc(
                it.oval!!,
                it.startDegree,  //开始角度
                it.endDegree,  //扫过的角度
                true,  //是否使用中心
                it.mPaint!!
            )

            drawLine(//从圆里面画出来的斜线
                it.slantLineStartX,
                it.slantLineStartY,
                it.slantLineEndX,
                it.slantLineEndY,
                linePaint
            )

            drawLine(//跟显示的文本挨着的横线
                it.textLineStartX,
                it.textLineStartY,
                it.textLineEndX,
                it.textLineEndY,
                linePaint
            )

            drawText(it.text, it.textX, it.textY - 6, textPaint)
        }
        if (isShowSmallCircular)
            drawArc(//中心小圆
                RectF(
                    (centerX - radius / 3),
                    (centerY - radius / 3),
                    (centerX + radius / 3),
                    (centerY + radius / 3)
                ),
                0f,  //开始角度
                360f,  //扫过的角度
                true,  //是否使用中心
                smallCircularPaint
            )
        if (isShowSplitLine) {
            mDatas.forEach {// 分割线
                calculatePosition(it.startDegree + 90, radius + peripheralWidth * 0.4f).apply {
                    drawLine(centerX, centerY, this[0], this[1], splitLinePaint)
                }
            }
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
                    val length = radius + peripheralWidth * 0.4f
                    oval = RectF(
                        centerX - length,
                        centerY - length,
                        centerX + length,
                        centerY + length
                    )
                }
                oldSelPosition = selPosition
            }
            foo?.invoke(oldSelPosition != -1, selPosition)
            postInvalidate()
        }
    }

    private var foo: ((Boolean, Int) -> Unit)? = null
    fun setItemClickListener(foo: (Boolean, Int) -> Unit) {
        this.foo = foo
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
        animator.cancel()
        oldSelPosition = -1

        var total = 0
        datas.forEach {
            total += it
        }
        angles = FloatArray(datas.size)
        var angle = 0f
        var index = 0
        ArrayList<PancakeData>().also { list ->
            datas.forEach {
                list.add(PancakeData(it / (total / 100f), "$it"))
                angle += 360f / 100f * (it / (total / 100f))
                angles!![index] = angle
                index++
            }
            list.setViewDataSources()
        }
    }

    private fun ArrayList<PancakeData>.setViewDataSources() {
        var index = 0
        var degree = 0f
        var position = 0
        var textX: Float
        var textY: Float
        var textLineEndX: Float? = null
        var startCoordinate0: FloatArray? = null
        var endCoordinate1: FloatArray? = null
        forEach {
            val newDegree = 360f / 100f * it.num
            val degreeTotal = degree + newDegree

            if (isStartAnim) {//有动画的
                calculatePosition(
                    degreeTotal + 90 - newDegree / 2,
                    radius * 0.76f
                ).also { coordinate ->
                    textX = coordinate[0] - textPaint.measureText(it.text) / 2
                    textY = coordinate[1] + measureHeight(textPaint) / 2
                }
            } else {//没动画的
                startCoordinate0 =
                    calculatePosition(degreeTotal + 90 - newDegree / 2, radius - peripheralWidth)
                endCoordinate1 =
                    calculatePosition(degreeTotal + 90 - newDegree / 2, radius + peripheralWidth)

                val textWidth = textPaint.measureText(it.text)
                val textHeight = measureHeight(textPaint)
                val lineX = endCoordinate1!![0]
                textLineEndX =
                    if (lineX > centerX) {
                        endCoordinate1!![0] + textWidth
                    } else {
                        endCoordinate1!![0] - textWidth
                    }
                textX = if (lineX > centerX) {
                    endCoordinate1!![0] + textWidth
                } else {
                    endCoordinate1!![0] - textWidth * 2
                }
                textY = endCoordinate1!![1] + textHeight / 2
            }

            it.startDegree = degree// 360度中开始的度
            it.endDegree = newDegree// 360度中结束的度
            it.slantLineStartX = if (!isStartAnim) startCoordinate0!![0] else -1f// 斜线线段的起始x坐标
            it.slantLineStartY = if (!isStartAnim) startCoordinate0!![1] else -1f// 斜线线段的起始y坐标
            it.slantLineEndX = if (!isStartAnim) endCoordinate1!![0] else -1f// 斜线线段的结束x坐标
            it.slantLineEndY = if (!isStartAnim) endCoordinate1!![1] else -1f// 斜线线段的结束y坐标
            it.textLineStartX = if (!isStartAnim) endCoordinate1!![0] else -1f
            it.textLineStartY = if (!isStartAnim) endCoordinate1!![1] else -1f
            it.textLineEndX = if (!isStartAnim) textLineEndX!! else -1f
            it.textLineEndY = if (!isStartAnim) endCoordinate1!![1] else -1f
            it.textX = textX// 文本的起始x坐标
            it.textY = textY// 文本的起始Y坐标
            it.mPaint = getPaint(randomColor(), 1, Paint.Style.FILL_AND_STROKE)
            it.position = position++
            it.oval = RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )
            mDatas.add(it)
            degree += newDegree
            index++
        }
        if (isStartAnim)
            animator.start()
        else postInvalidate()
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

    /**
     * 生成随机颜色
     */
    private fun randomColor(): Int {
        val color = Random().let {
            Color.rgb(
                it.nextInt(255),
                it.nextInt(255),
                it.nextInt(255)
            )
        }
        return if (color != mBackgroundColor && color != mTextColor && color != mLineColor) {
            color
        } else randomColor()
    }
}

data class PancakeData(
    val num: Float,// 100中的几份
    val text: String = "",// 显示的文本
) {
    var startDegree: Float = -1.0f// 360度中开始的度
    var endDegree: Float = -1.0f//360度中结束的度
    var slantLineStartX: Float = -1.0f// 斜线线段的起始x坐标
    var slantLineStartY: Float = -1.0f// 斜线线段的起始y坐标
    var slantLineEndX: Float = -1.0f// 斜线线段的结束x坐标
    var slantLineEndY: Float = -1.0f// 斜线线段的结束y坐标
    var textLineStartX: Float = -1.0f//
    var textLineStartY: Float = -1.0f//
    var textLineEndX: Float = -1.0f//
    var textLineEndY: Float = -1.0f//
    var textX: Float = -1.0f// 文本的起始x坐标
    var textY: Float = -1.0f// 文本的起始Y坐标
    var mPaint: Paint? = null//
    var oval: RectF? = null//
    var position: Int = -1//
}