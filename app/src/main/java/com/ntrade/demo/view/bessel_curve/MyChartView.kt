package com.ntrade.demo.view.bessel_curve

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import com.ntrade.demo.R
import kotlin.math.roundToInt


/** * 创建者：leiwu
 * * 时间：2022/8/23 16:26
 * * 类描述：
 * * 修改人：
 * * 修改时间：    private val gestureDetector by lazy { GestureDetectorCompat(context, this) }
 * * 修改备注：GestureDetector.OnGestureListener
 */
class MyChartView : View, GestureDetector.OnGestureListener {
    private var isShowSpot = true// 曲线上的点是否显示
    private var isShowBezierVerticalLine = true//曲线到底的竖线是否显示
    private var isLeft = false
    private var isShowGradualBackground = true//是否显示渐变底色
    private var isCompelCanScroll = false//是否可以滚动,可以在布局中设置的参数
    private var startX = 0f//图表的的起始X坐标
    private var startY = 0f//图表的的起始Y坐标
    private var endX = 0f//图表的的起始X坐标
    private var endY = 0f//图表的的起始Y坐标
    private var blankWidth = 0f//左或右空白的宽度
    private var blankHeight = 0f//上或下空白的高度
    private var chartWidth = 0f//图表的宽度
    private var chartHeight = 0f//图表的高度
    private var spotRadius = 10f//圆点的半径


    private var mHeight = 0f//View的高
    private var mWidth = 0f//View的宽
    private val margin = 16f
    private var aPartWidth = 0f
    private var mScroolX = 0f
    private var isDown = false
    private var isCanScroll = false//是否可以滚动

    private val values by lazy { ArrayList<Int>() }

    private val mPoints = ArrayList<PointF>()
    private val gradualBackgroundPath by lazy { Path() }
    private val bezierCurvePath by lazy { Path() }

    private val linePaint by lazy {
        getPaint(getColor(R.color.bessel_line), 4, Paint.Style.STROKE)
    }
    private val formPaint by lazy {
        getPaint(getColor(R.color.bessel_from_line), 1, Paint.Style.STROKE)
    }
    private val noodlesPaint by lazy {
        getPaint(getColor(R.color.teal_700), 1, Paint.Style.STROKE)
    }
    private val spotPaint by lazy {
        getPaint(getColor(R.color.bessel_text), 1, Paint.Style.FILL_AND_STROKE)
    }
    private val backgroundPaint by lazy {
        getPaint(mBackgroundColor, 1, Paint.Style.FILL_AND_STROKE)
    }
    private val textPaint by lazy {
        getPaint(
            getColor(R.color.bessel_text),
            1,
            Paint.Style.FILL_AND_STROKE
        ).apply {
            textSize = dp2px(10f).toFloat()
        }
    }
    private val gestureDetector by lazy { GestureDetectorCompat(context, this) }
    private val overScroller by lazy { OverScroller(context) }

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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()//控件的宽
        mHeight = h.toFloat()//控件的高  TSW19740324
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mPoints.isEmpty()) return
        gradualBackgroundPath.reset()
        bezierCurvePath.reset()
        canvas?.apply {
//            scroolTest()
            drawForm()// 底格的横线
            save()
            translate(mScroolX, 0f)// 平移
            drawBottomNumber()// 画底部的数字
            for (x in 0 until mPoints.size - 1) {
                bezierCurvePath.moveTo(mPoints[x].x, mPoints[x].y)
                getBezierCurveValue(mPoints[x], mPoints[x + 1]) { p1, p2, p3 ->
                    //贝瑟尔曲线
                    bezierCurvePath.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
                    //贝瑟尔曲线的渐变
                    if (isShowGradualBackground) setGradualBackground(x, p1, p2, p3)
                }
            }
            //贝瑟尔曲线的渐变
            if (isShowGradualBackground) drawPath(gradualBackgroundPath, noodlesPaint)
            //贝瑟尔曲线
            drawPath(bezierCurvePath, linePaint)
            mPoints.forEach {
                drawSpotAndLine(it)//贝瑟尔曲线上的点
            }
            restore()
            drawScale()
        }
    }

    /**
     * 画刻度上的字和刻度的竖线和横线
     */
    private fun Canvas.drawScale() {
        Path().also { mPath ->// 左或右竖着字的北京
            if (isLeft) {
                mPath.moveTo(endX, 0f) //移动画笔到指定位置
                mPath.lineTo(mWidth, 0f)
                mPath.lineTo(mWidth, mHeight)
                mPath.lineTo(endX, mHeight)
                mPath.close()
            } else {
                mPath.moveTo(0f, 0f) //移动画笔到指定位置
                mPath.lineTo(startX, 0f)
                mPath.lineTo(startX, mHeight)
                mPath.lineTo(0f, mHeight)
                mPath.close()
            }
            drawPath(mPath, backgroundPaint)
        }

        val h = chartHeight / 10
        val param = 14f
        val strHeight = measureHeight(textPaint) / 2
        var strX: Float
        var strY: Float
        var str: String
        for (x in 0..10) {//画横线和横着的数字
            str = "${(10 - x) * 10}"
            strX =
                if (isLeft) {
                    endX + param
                } else {
                    startX - textPaint.measureText(str) - param
                }
            strY = (h * x + startY) + strHeight
            drawText(str, strX, strY, textPaint)
        }
        if (isLeft) {//表格最外的横线和竖线
            drawLine(endX, startY, endX, endY, linePaint)
            drawLine(0f, endY, endX, endY, linePaint)
        } else {
            drawLine(startX, startY, startX, endY, linePaint)
            drawLine(startX, endY, mWidth, endY, linePaint)
        }
    }

    private fun Canvas.scroolTest() {
        translate(mScroolX, 0f)
        Path().also { mPath ->
            mPath.moveTo(50f, 50f) //移动画笔到指定位置
            mPath.lineTo(200f, 50f)
            mPath.lineTo(200f, 300f)
            mPath.lineTo(50f, 300f)
            mPath.close()
            drawPath(mPath, spotPaint)
        }
        Path().also { mPath ->
            mPath.moveTo(300f, 50f) //移动画笔到指定位置
            mPath.lineTo(1300f, 50f)
            mPath.lineTo(1300f, 300f)
            mPath.lineTo(300f, 300f)
            mPath.close()
            drawPath(mPath, spotPaint)
        }
    }

    /**
     * 画底部的数字
     */
    private fun Canvas.drawBottomNumber() {
        val strHeight = measureHeight(textPaint) / 2
        var strX: Float
        var strY: Float
        var str: String
        for (x in 0 until values.size) {//画底部的数字
            str = "${values[x]}"
            val textWidth = textPaint.measureText(str)
            strX = mPoints[x].x - textWidth / 2
            strY = endY + strHeight * 2 + spotRadius
            drawText(str, strX, strY, textPaint)
        }
    }

    /**
     * 画贝瑟尔曲线的点和点到底部的竖线
     */
    private fun Canvas.drawSpotAndLine(point: PointF) {
        //贝瑟尔曲线到底的竖线
        if (isShowBezierVerticalLine)
            drawLine(
                point.x,
                point.y,
                point.x,
                endY,
                formPaint
            )
        //贝瑟尔曲线上的点
        if (isShowSpot)
            drawArc(
                RectF(//弧线所使用的矩形区域大小
                    point.x - spotRadius,
                    point.y - spotRadius,
                    point.x + spotRadius,
                    point.y + spotRadius
                ),
                0f,  //开始角度
                -360f,  //扫过的角度
                true,  //是否使用中心
                spotPaint
            )
    }

    private fun setGradualBackground(x: Int, p1: PointF, p2: PointF, p3: PointF) {
        gradualBackgroundPath.moveTo(mPoints[x].x, mPoints[x].y)
        gradualBackgroundPath.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
        gradualBackgroundPath.lineTo(p3.x, endY)
        gradualBackgroundPath.lineTo(mPoints[x].x, endY)
        noodlesPaint.alpha = 255
        noodlesPaint.style = Paint.Style.FILL

        noodlesPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            endY,
            intArrayOf(getColor(R.color.bessel_gradual1), getColor(R.color.bessel_gradual2)),
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun getColor(id: Int): Int = context.resources.getColor(id)

    /**
     * 底格的横线
     */
    private fun Canvas.drawForm() {
        val h = chartHeight / 10
        var startX: Float
        var startY: Float
        var stopX: Float
        var stopY: Float
        for (x in 0..10) {//画横线和横着的数字
            startX = 0f
            startY = h * x + this@MyChartView.startY
            stopX = mWidth
            stopY = h * x + this@MyChartView.startY
            drawLine(startX, startY, stopX, stopY, formPaint)
        }
    }

    /**
     * 获取三次贝瑟尔曲线的值
     * @param startPoint    当前要画的点
     * @param endPoint      下一个要画的点
     */
    private fun getBezierCurveValue(
        startPoint: PointF,
        endPoint: PointF,
        foo: (PointF, PointF, PointF) -> Unit
    ) {
        val wt = (startPoint.x + endPoint.x) / 2
        foo.invoke(PointF(wt, startPoint.y), PointF(wt, endPoint.y), endPoint)
    }

    private var mBackgroundColor = 0

    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.chart).apply {
            mBackgroundColor = getColor(
                R.styleable.chart_background_color,
                Color.parseColor("#202836")
            )
            setBackgroundColor(mBackgroundColor)
        }
    }

    fun mInvalidate(list: ArrayList<Int>) {
        mPoints.clear()
        values.clear()
        values.addAll(list)

        initFoundationSize(list.size) {
            var index = 0
            list.forEach {
                val x =
                    if (isLeft) aPartWidth * index + startX
                    else aPartWidth * index + blankWidth + margin

                val y = (chartHeight / 100) * (100f - it) + startY
                mPoints.add(PointF(x, y))
                index++
            }
        }

        postInvalidate()
    }

    /**
     * 每次更新数据最好初始化一下基础尺寸，以免图因为数据量不同而混乱
     */
    private fun initFoundationSize(size: Int, foo: () -> Unit) {
        blankWidth = textPaint.measureText("100")//空白处的宽度
        blankHeight = measureHeight(textPaint).toFloat() + margin * 2 + spotRadius
        aPartWidth =
            if (isCompelCanScroll) {
                (mWidth - blankWidth - margin * 2) / (size - 1)
            } else {
                if (size > 6) mWidth / 10f
                else (mWidth - blankWidth - margin * 2) / (size - 1)
            }
        if (isLeft) {
            startX = mWidth - (margin + blankWidth + aPartWidth * size) + aPartWidth-spotRadius
            endX = mWidth - margin - blankWidth
        } else {
            startX = blankWidth + margin
            endX = margin + blankWidth + aPartWidth * size
        }
        startY = margin
        endY = margin + mHeight - blankHeight
        chartWidth = endX - startX
        chartHeight = endY - startY
        isCanScroll = isCompelCanScroll && chartWidth > mWidth - (blankWidth + margin)
        foo.invoke()
        blankWidth += aPartWidth
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

    /**
     * 获取要画的TextView的高度
     * todo 获取宽度可以直接用 TextView 的 Paint 调用measureText("字符串")
     * todo textView.paint.measureText("字符串");
     */
    private fun measureHeight(paint: Paint): Int {
        val fm = paint.fontMetricsInt
        return fm.top.inv() - (fm.top.inv() - fm.ascent.inv()) - (fm.bottom - fm.descent)
    }

    private fun dp2px(dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        log("isCanScroll = $isCanScroll")
        return if (isCanScroll) {
            event?.apply {
                when (action) {
                    MotionEvent.ACTION_DOWN -> isDown = true
                    MotionEvent.ACTION_UP -> isDown = false
                    MotionEvent.ACTION_CANCEL -> isDown = false
                }
            }
            gestureDetector.onTouchEvent(event)
            true
        } else super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            if (!isDown) {
                scrollTo(overScroller.currX, overScroller.currY)
                postInvalidate()
            }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        val oldX = mScroolX
        mScroolX = x.toFloat()
        checkMScroolX()
        if (mScroolX != oldX) {
            onScrollChanged(mScroolX.toInt(), 0, oldX.toInt(), 0)
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    private fun checkMScroolX() {
        if (isLeft) {
            val leftBoundary = chartWidth - mWidth + blankWidth - aPartWidth + margin * 2
            when {
                mScroolX > leftBoundary -> mScroolX = leftBoundary
                mScroolX < -margin -> mScroolX = -margin
            }
        } else {
            when {
                mScroolX > 0 -> mScroolX = margin
                mScroolX < mWidth - chartWidth + margin -> mScroolX = mWidth - chartWidth
            }
        }
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        mScroolX -= distanceX
        checkMScroolX()
        log("mScroolX = $mScroolX")
        postInvalidate()
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        overScroller.fling(
            mScroolX.toInt(), 0, (velocityX / 2).roundToInt(), 0, Int.MIN_VALUE, Int.MAX_VALUE,
            0, 0
        )
        return true
    }

    private fun log(str: String) {
        Log.d("MYCHARTVIEW1", str)
    }
}