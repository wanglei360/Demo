package com.ntrade.demo.view.chart

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import com.ntrade.demo.R
import com.ntrade.demo.bean.MChartData
import com.ntrade.demo.bean.MChartPointPositionInfo
import kotlin.math.abs
import kotlin.math.roundToInt


/** * 创建者：leiwu
 * * 时间：2022/8/23 16:26
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 备注：使用该View需要在清单文件中添加震动权限 android.permission.VIBRATE ，不需要申请用户同意,长按时有用到
 * * 经测试，数据量达到5万条数据仍然没有丝毫卡顿，优化过，就算是五百万，除非是内存溢出或计算的数字类型int或float超长会崩溃，否则没有问题
 */
class MyChartView : View, GestureDetector.OnGestureListener {

    private var isShowLine = true//线是否显示
    private var isShowSpotToBottomLine = true//点到底的竖线是否显示
    private var isShowSpot = true//点是否显示
    private var isLeft = true // 图在屏幕的左边是true ,否则是false
    private var isShowGradualBackground = true//是否显示渐变底色
    private var isCompelCanScroll = true//是否可以滚动,可以在布局中设置的参数
    private var isShowAnim = true//是否展示动画
    private var spotRadius = 10f//圆点的半径
    private var mBackgroundColor = 0//底色
    private var columnWidth = 50f//柱状图柱子的宽度
    private var columuMargin = 16f//柱状图距离左右两边的距离

    private val CHART_LINE = 0//线
    private val CHART_BEZIER_CURVE = 1//曲线
    private val CHART_COLUMN = 2//柱状图
    private var chartType = CHART_COLUMN

    private var startX = 0f//图表的的起始X坐标
    private var startY = 0f//图表的的起始Y坐标
    private var endX = 0f//图表的的结束X坐标
    private var endY = 0f//图表的的结束Y坐标
    private var hundredWidth = 0f//100的文本宽度   刻度上第一个数字的宽度
    private var blankWidth = 0f//左或右空白的宽度 设置值的时候，多加了一个一份的宽度，不加曲线不好弄
    private var blankHeight = 0f//上或下空白的高度
    private var chartWidth = 0f//图表的宽度
    private var chartHeight = 0f//图表的高度
    private var mHeight = 0f//View的高
    private var mWidth = 0f//View的宽
    private var textAndLineClearance = 14f
    private val margin = 26f
    private var aPartWidth = 0f
    private var mScroolX = 0f
    private var mAnimValue = 0f
    private var maxNum = 100
    private var animWidth = 0f
    private var animTime = 1000L
    private var longPressX = -1f
    private var longPressY = -1f
    private var firstShowPosition = -1
    private var lastShowPosition = -1
    private var bottomStrWidth = -1f
    private var bottomStrHeight = -1
    private var oldSelPosition = -1
    private var isDown = false
    private var isCanScroll = true//是否可以滚动
    private var isAniming = false
    private var isLongPress = false//长按

    /**
     * position,isSel,pointX,pointY
     */
    private var itemClickListener: ((Int, Boolean, Float, Float) -> Unit)? = null
    private val datas by lazy { ArrayList<MChartData>() }
    private val mPoints = ArrayList<PointF>()

    private val bezierCurve = ArrayList<ArrayList<PointF>>()
    private val positionInfos = ArrayList<MChartPointPositionInfo>()
    private val mPath by lazy { Path() }
    private val gradualBackgroundPath by lazy { Path() }
    private val bezierCurvePath by lazy { Path() }

    private val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val animator by lazy {
        ValueAnimator.ofFloat(1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            addUpdateListener { animation ->
                mAnimValue = animation.animatedValue.toString().toFloat()
                postInvalidate()
            }
            addListener(MAnimatorListener {
                isAniming = it
            })
            duration = animTime
        }
    }

    private val linePaint by lazy {
        getPaint(getColor(R.color.bessel_line), 4, Paint.Style.STROKE)
    }

    private val formPaint by lazy {
        getPaint(getColor(R.color.bessel_from_line), 2, Paint.Style.STROKE)
    }

    private var linearGradientColors =
        intArrayOf(getColor(R.color.bessel_gradual1), getColor(R.color.bessel_gradual2))

    private val linearGradientPaint by lazy {
        getPaint(getColor(R.color.teal_700), 1, Paint.Style.FILL).apply {
            alpha = 255
            shader =
                LinearGradient(0f, 0f, 0f, endY, linearGradientColors, null, Shader.TileMode.CLAMP)
        }
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

    constructor(context: Context?) : super(context)
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
        textAndLineClearance = mWidth / 77
        setDatas()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            if (mPoints.isEmpty()) {
                setDefaultChart()
                return
            }

            drawForm()// 底格的横线
            save()
            translate(mScroolX, 0f)// 平移
            drawChartLine()//曲线和渐变的背景
            drawSpotAndLine()//曲线上的点 和 曲线到底的竖线
            drawBottomNumber()// 画底部的数字
            restore()
            drawScale()// 画刻度上的字和刻度的竖线和横线
            //画一个蒙版越来越小来当做动画,用其他方法计算找能用的方法实在是太费劲了，还不如这个方法简单
            if (isShowAnim) mAnim()
            if (isLongPress) drawLongPressLine()
        }
    }

    private fun MotionEvent.setOnItemClick() {
        if (isLeft) {
            val firstPosition = mPoints.size - 1 - firstShowPosition

            val selPosition = (x / aPartWidth + firstPosition).toInt()
            val selPointX = mPoints[selPosition].x + mScroolX - (aPartWidth * selPosition)
            val selPointY = mPoints[selPosition].y


        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (isCanScroll && !isAniming) {
            event?.apply {
                when (action) {
                    MotionEvent.ACTION_DOWN -> isDown = true
                    MotionEvent.ACTION_UP -> {
                        isLongPress = false
                        isDown = false
                        itemClickListener?.invoke(-1, false, -1f, -1f)
                        postInvalidate()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        isDown = false
                        isLongPress = false
                        itemClickListener?.invoke(-1, false, -1f, -1f)
                        postInvalidate()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        itemClickListener?.also {
                            longPressX = x
                            longPressY = y
                            postInvalidate()
                            if (isLongPress) {
                                val selPosition =
                                    if (isLeft) {
                                        val firstPointX =
                                            mPoints[lastShowPosition].x + mScroolX - (aPartWidth * lastShowPosition)
                                        ((x - firstPointX) / aPartWidth).toInt()
                                    } else {
                                        val firstPointX = mPoints[firstShowPosition].x + mScroolX
                                        ((x - firstPointX) / aPartWidth).toInt() + firstShowPosition
                                    }
                                val smallPosition =
                                    if (selPosition < 1) 0 else selPosition - 1

                                val bigPosition =
                                    if (selPosition > mPoints.size - 1) mPoints.size - 1 else selPosition + 1

                                val firstXNum: Float
                                val lastXNum: Float
                                if (chartType == CHART_COLUMN) {
                                    firstXNum = mScroolX - columnWidth / 2
                                    lastXNum = mScroolX + columnWidth / 2
                                } else {
                                    firstXNum = mScroolX - linePaint.strokeWidth - spotRadius
                                    lastXNum = mScroolX + linePaint.strokeWidth + spotRadius
                                }
                                for (index in smallPosition..bigPosition) {
                                    try {
                                        val pointFirstX = mPoints[index].x + firstXNum
                                        val pointLastX = mPoints[index].x + lastXNum
                                        if (x in pointFirstX..pointLastX) {
                                            checkClick(
                                                index,
                                                mPoints[index].x + mScroolX,
                                                mPoints[index].y
                                            )
                                            return true
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                                itemClickListener?.invoke(-1, false, -1f, -1f)
                                oldSelPosition = -1
                            }
                        }
                    }
                }
            }
            event?.apply {
                gestureDetector.onTouchEvent(this)
            }
            true
        } else super.onTouchEvent(event)
    }

    /**
     * onFling 方法中调用fling时，此方法会被调用
     */
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

    /**
     * 手指在屏幕上左右滑动时调用
     */
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        mScroolX -= distanceX
        checkMScroolX()
        postInvalidate()
        return true
    }

    /**
     * 长按回调
     */
    override fun onLongPress(e: MotionEvent?) {
        itemClickListener?.apply {
            shock {
                isLongPress = true
            }
        }
    }

    /**
     * 手指在屏幕上滑动抬起时，需要惯性滚动，此方法被调用
     * @param velocityX 横着滚动的速度
     */
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

    private fun Canvas.setDefaultChart() {
        drawForm()// 底格的横线
        drawScale()// 画刻度上的字和刻度的竖线和横线
    }

    private fun Canvas.drawChartLine() {
        when (chartType) {
            CHART_LINE -> drawLine()
            CHART_BEZIER_CURVE -> drawBezier()//曲线和渐变底色
            CHART_COLUMN -> drawColumn()
        }
    }

    private fun Canvas.drawColumn() {
        getLoopNums(bezierCurve) { smallNum, bigNum ->
            val strokeWidth = linePaint.strokeWidth
            for (x in smallNum..bigNum) {
                try {
                    // 渐变的底色
                    if (isShowGradualBackground) {
                        gradualBackgroundPath.reset()
                        gradualBackgroundPath.moveTo(
                            bezierCurve[x][0].x,
                            bezierCurve[x][0].y
                        )
                        gradualBackgroundPath.lineTo(
                            bezierCurve[x][1].x,
                            bezierCurve[x][1].y
                        )
                        gradualBackgroundPath.lineTo(
                            bezierCurve[x][2].x,
                            bezierCurve[x][2].y
                        )
                        gradualBackgroundPath.lineTo(
                            bezierCurve[x][3].x,
                            bezierCurve[x][3].y
                        )
                        drawPath(gradualBackgroundPath, linearGradientPaint)
                    }

                    if (isShowLine) {
                        // 柱状图
                        bezierCurvePath.reset()
                        bezierCurvePath.moveTo(bezierCurve[x][0].x, bezierCurve[x][0].y)
                        bezierCurvePath.lineTo(bezierCurve[x][1].x, bezierCurve[x][1].y)
                        bezierCurvePath.lineTo(bezierCurve[x][2].x, bezierCurve[x][2].y)
                        bezierCurvePath.lineTo(bezierCurve[x][3].x, bezierCurve[x][3].y)
                        drawPath(bezierCurvePath, linePaint)
                    }
                } catch (e: Exception) {
                    log("")
                }
            }
        }
    }

    private fun Canvas.drawLine() {
        getLoopNums(mPoints) { smallNum, bigNum ->
            for (x in smallNum..bigNum) {
                if (isShowGradualBackground && x < mPoints.size - 1) {
                    gradualBackgroundPath.reset()
                    gradualBackgroundPath.moveTo(mPoints[x].x, mPoints[x].y)
                    gradualBackgroundPath.lineTo(mPoints[x].x, endY)
                    gradualBackgroundPath.lineTo(mPoints[x + 1].x, endY)
                    gradualBackgroundPath.lineTo(mPoints[x + 1].x, mPoints[x + 1].y)
                    gradualBackgroundPath.close()
                    drawPath(gradualBackgroundPath, linearGradientPaint)
                }

                if (isShowLine && x < mPoints.size - 1) {
                    drawLine(
                        mPoints[x].x,
                        mPoints[x].y,
                        mPoints[x + 1].x,
                        mPoints[x + 1].y,
                        linePaint
                    )
                }
            }
        }
    }

    private fun Canvas.drawBezier() {
        getLoopNums(bezierCurve) { smallNum, bigNum ->
            for (x in smallNum..bigNum) {
                bezierCurve[x].also {
                    if (it.isNotEmpty()) {
                        //贝瑟尔曲线的渐变
                        if (isShowGradualBackground) drawGradualBackground(it)
                        //贝瑟尔曲线
                        if (isShowLine) drawBezierCurve(it)
                    }
                }
            }
        }
    }

    private fun Canvas.drawBezierCurve(point: ArrayList<PointF>) {
        bezierCurvePath.reset()
        bezierCurvePath.moveTo(point[0].x, point[0].y)
        bezierCurvePath.cubicTo(
            point[1].x,
            point[1].y,
            point[2].x,
            point[2].y,
            point[3].x,
            point[3].y
        )
        drawPath(bezierCurvePath, linePaint)
    }

    private fun Canvas.drawGradualBackground(point: ArrayList<PointF>) {
        gradualBackgroundPath.reset()
        gradualBackgroundPath.moveTo(point[0].x, point[0].y)
        gradualBackgroundPath.cubicTo(
            point[1].x,
            point[1].y,
            point[2].x,
            point[2].y,
            point[3].x,
            point[3].y
        )
        gradualBackgroundPath.lineTo(point[3].x, endY)
        gradualBackgroundPath.lineTo(point[0].x, endY)
        drawPath(gradualBackgroundPath, linearGradientPaint)
    }

    private fun Canvas.drawLongPressLine() {
        drawLine(longPressX, 0f, longPressX, mHeight, linePaint)
        drawLine(0f, longPressY, mWidth, longPressY, linePaint)
    }

    private fun Canvas.mAnim() {
        mPath.reset()
        if (isLeft) {
            mPath.moveTo(0f, 0f)
            mPath.lineTo(0f, endY - linePaint.strokeWidth / 2)
            mPath.lineTo(animWidth * mAnimValue, endY - linePaint.strokeWidth / 2)
            mPath.lineTo(animWidth * mAnimValue, 0f)
        } else {
            mPath.moveTo(mWidth - (animWidth * mAnimValue), 0f)
            mPath.lineTo(mWidth - (animWidth * mAnimValue), endY - linePaint.strokeWidth / 2)
            mPath.lineTo(mWidth, endY - linePaint.strokeWidth / 2)
            mPath.lineTo(mWidth, 0f)
        }
        mPath.close()
        drawPath(mPath, backgroundPaint)

        val h = chartHeight / 10
        val startX =
            if (isLeft) animWidth * mAnimValue
            else mWidth
        var startY: Float
        val stopX =
            if (isLeft) 0f
            else mWidth - (animWidth * mAnimValue)

        var stopY: Float
        for (x in 0..9) {//画横线和横着的数字
            startY = h * x + this@MyChartView.startY
            stopY = h * x + this@MyChartView.startY
            drawLine(startX, startY, stopX, stopY, formPaint)
        }
    }

    /**
     * 画刻度上的字和刻度的竖线和横线
     */
    private fun Canvas.drawScale() {
        val h = chartHeight / 10
        var strX: Float
        var strY: Float
        var str: String

        mPath.reset()// 左或右竖着字的背景
        if (isLeft) {
            mPath.moveTo(endX, 0f)
            mPath.lineTo(endX, endY)
            mPath.lineTo(endX + hundredWidth / 2, endY)
            mPath.lineTo(endX + hundredWidth / 2, mHeight)
            mPath.lineTo(mWidth, mHeight)
            mPath.lineTo(mWidth, 0f)
            mPath.close()
        } else {
            mPath.moveTo(0f, 0f)
            mPath.lineTo(startX, 0f)
            mPath.lineTo(startX, endY)
            mPath.lineTo(startX - hundredWidth / 2, endY)
            mPath.lineTo(startX - hundredWidth / 2, mHeight)
            mPath.lineTo(0f, mHeight)
            mPath.close()
        }
        drawPath(mPath, backgroundPaint)

        val aPart = maxNum / 10
        for (x in 10 downTo 0) {//画横线和横着的数字
            str = "${aPart * x}"
            strX =
                if (isLeft) {
                    endX + textAndLineClearance
                } else {
                    startX - textPaint.measureText(str) - textAndLineClearance
                }
            strY = h * (10 - x) + startY
            drawText(str, strX, strY, textPaint)
        }
        if (isLeft) {//表格最外的横线和竖线
            drawLine(endX, startY, endX, endY, formPaint)
            drawLine(0f, endY, endX, endY, formPaint)
        } else {
            drawLine(startX, startY, startX, endY, formPaint)
            drawLine(startX, endY, mWidth, endY, formPaint)
        }
    }

    /**
     * 画底部的数字
     */
    private fun Canvas.drawBottomNumber() {
        mPath.reset()
        if (isLeft) {
            mPath.moveTo(startX - spotRadius, endY)
            mPath.lineTo(startX - spotRadius, mHeight)
            mPath.lineTo(endX, mHeight)
            mPath.lineTo(endX, endY)
        } else {
            mPath.moveTo(0f, endY)
            mPath.lineTo(0f, mHeight)
            mPath.lineTo(endX + spotRadius + textAndLineClearance, mHeight)
            mPath.lineTo(endX + spotRadius + textAndLineClearance, endY)
        }
        mPath.close()
        drawPath(mPath, backgroundPaint)

        bottomStrHeight = measureHeight(textPaint) / 2
        var strX: Float
        var strY: Float
        var str: String
        getLoopNums(mPoints) { smallNum, bigNum ->
            for (x in smallNum..bigNum) {//画底部的数字
                str = datas[x].bottomStr
                val textWidth = textPaint.measureText(str)
                strX = mPoints[x].x - textWidth / 2
                strY = endY + bottomStrHeight * 2 + spotRadius
                drawText(str, strX, strY, textPaint)
            }
        }
    }

    /**
     * 画贝瑟尔曲线的点和点到底部的竖线
     */
    private fun Canvas.drawSpotAndLine() {
        getLoopNums(mPoints) { smallNum, bigNum ->
            for (x in smallNum..bigNum) {
                //曲线到底的竖线
                if (isShowSpotToBottomLine)
                    drawLine(
                        mPoints[x].x,
                        mPoints[x].y,
                        mPoints[x].x,
                        endY,
                        formPaint
                    )
                //曲线上的点
                if (isShowSpot)
                    drawArc(
                        RectF(//弧线所使用的矩形区域大小
                            mPoints[x].x - spotRadius,
                            mPoints[x].y - spotRadius,
                            mPoints[x].x + spotRadius,
                            mPoints[x].y + spotRadius
                        ),
                        0f,  //开始角度
                        -360f,  //扫过的角度
                        true,  //是否使用中心
                        spotPaint
                    )
            }
        }
    }

    /**
     * 底格的横线
     */
    private fun Canvas.drawForm() {
        val h = chartHeight / 10
        var startX: Float
        var startY: Float
        var stopX: Float
        var stopY: Float
        for (x in 0..9) {//画横线
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

    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.chart).apply {
            mBackgroundColor = getColor(
                R.styleable.chart_background_color,
                Color.parseColor("#202836")
            )
            setBackgroundColor(mBackgroundColor)
            linePaint.color = getColor(
                R.styleable.chart_line_color,
                Color.parseColor("#24FBB5")
            )
            formPaint.color = getColor(
                R.styleable.chart_form_color,
                Color.parseColor("#a9a9a9")
            )
            linearGradientColors = intArrayOf(
                getColor(
                    R.styleable.chart_linear_gradient_color_top,
                    Color.parseColor("#FF018786")
                ),
                getColor(
                    R.styleable.chart_linear_gradient_color_bottom,
                    Color.parseColor("#8015AE9E")
                )
            )

            spotPaint.color = getColor(
                R.styleable.chart_spot_color,
                Color.parseColor("#F4FBFF")
            )
            textPaint.color = getColor(
                R.styleable.chart_text_color,
                Color.parseColor("#F4FBFF")
            )
            textPaint.textSize = getDimension(R.styleable.chart_scale_text_size, 30f)
            isShowLine =
                getBoolean(R.styleable.chart_is_show_line, true)
            isShowSpotToBottomLine =
                getBoolean(R.styleable.chart_is_show_spot_to_bottom_line, true)
            isShowSpot =
                getBoolean(R.styleable.chart_is_show_spot_to_bottom_line, true)
            isLeft = getBoolean(R.styleable.chart_is_left, true)
            isShowGradualBackground = getBoolean(R.styleable.chart_is_show_gradual_background, true)
            isCompelCanScroll = getBoolean(R.styleable.chart_is_compel_can_scroll, true)
            isShowAnim = getBoolean(R.styleable.chart_is_show_anim, true)
            spotRadius = getDimension(R.styleable.chart_spot_radius, 10f)
            columnWidth = getDimension(R.styleable.chart_column_width, 50f)
            columuMargin = getDimension(R.styleable.chart_columu_margin, 16f)
            animTime = getInt(R.styleable.chart_anim_time, 1000).toLong()
            chartType = getInt(R.styleable.chart_chart_type, 0)
            if (chartType == CHART_COLUMN) {
                isShowSpot = false
                isShowSpotToBottomLine = false
            }
        }
    }

    /**
     * 每次更新数据最好初始化一下基础尺寸，以免图因为数据量不同而混乱
     */
    private fun initFoundationSize(foo: (() -> Unit)? = null) {
        bottomStrWidth = -1f
        val size =
            if (datas.isEmpty()) 1
            else datas.size

        hundredWidth = textPaint.measureText("$maxNum")

        blankWidth = hundredWidth + textAndLineClearance + margin//空白处的宽度
        blankHeight = (measureHeight(textPaint).toFloat() + margin + spotRadius) * 2

        aPartWidth =
            if (chartType == CHART_COLUMN) columnWidth + columuMargin * 2 else mWidth / 7

        aPartWidth =
            when {
                chartType == CHART_COLUMN -> {
                    if (aPartWidth > hundredWidth + columuMargin * 2) aPartWidth else hundredWidth + columuMargin * 2
                }
                hundredWidth > aPartWidth -> hundredWidth
                else -> {
                    aPartWidth
                }
            }.let {
                val bottomStr = if (datas.isEmpty()) "" else datas[0].bottomStr
                val bottomStrWidth = textPaint.measureText(bottomStr)
                val width = if (it > bottomStrWidth) it else bottomStrWidth
                if (this@MyChartView.bottomStrWidth < bottomStrWidth)
                    this@MyChartView.bottomStrWidth = bottomStrWidth
                width + textAndLineClearance
                width
            }

        if (isLeft) {
            startX =
                if (chartType == CHART_COLUMN) mWidth - (aPartWidth * size + blankWidth) + columnWidth / 2
                else mWidth - (blankWidth + aPartWidth * size) + aPartWidth - spotRadius
            endX = mWidth - blankWidth
        } else {
            startX = blankWidth
            endX =
                if (chartType == CHART_COLUMN) startX + aPartWidth * size
                else startX + aPartWidth * size
        }
        startY = measureHeight(textPaint).toFloat() + spotRadius
        endY = mHeight - blankHeight / 2
        chartWidth = endX - startX
        chartHeight = endY - startY
        isCanScroll = isCompelCanScroll && chartWidth > mWidth - (hundredWidth + margin)
        foo?.invoke()
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
     * 获取要画的文本的高度
     * todo 获取宽度可以直接用 TextView 的 Paint 调用measureText("字符串")
     * todo textView.paint.measureText("字符串");
     */
    private fun measureHeight(paint: Paint): Int {
        val fm = paint.fontMetricsInt
        return fm.top.inv() - (fm.top.inv() - fm.ascent.inv()) - (fm.bottom - fm.descent)
    }

    private fun dp2px(value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun checkClick(index: Int, pointX: Float, pointY: Float) {
        if (oldSelPosition != index) {
            oldSelPosition = index
            itemClickListener?.invoke(
                if (isLeft) {
                    mPoints.size - 1 - index
                } else index, true, pointX, pointY
            )
        }
    }

    private fun checkMScroolX() {
        if (isLeft) {
            val leftBoundary =
                chartWidth + blankWidth - aPartWidth - mWidth + bottomStrWidth / 2 + mWidth / 50

            val num = mWidth / 50 * -1
            when {
                mScroolX > leftBoundary -> mScroolX = leftBoundary
                mScroolX < num -> mScroolX = num
            }
        } else {
            val bigNum = if (spotRadius > bottomStrWidth / 2) spotRadius else bottomStrWidth / 2
            val num =
                (chartWidth - mWidth - margin * 2) * -1 - bigNum - aPartWidth / 2
            when {
                mScroolX > bigNum -> mScroolX = bigNum
                mScroolX < num -> mScroolX = num
            }
        }
        setFirstShowInfo()
    }

    /**
     * 不能优化掉该方法，必须实时调用
     * 画曲线和点时需要用到 firstShowPosition 和 lastShowPosition
     * 如果这两个值不准，画的图会出错
     */
    private fun setFirstShowInfo() {
        positionInfos.clear()
        if (isLeft) {
            firstShowPosition = ((mScroolX - 10 + aPartWidth) / aPartWidth).toInt()
            val pointPosition = mPoints.size - 1 - firstShowPosition
            var index = 0
            for (x in pointPosition downTo 0) {
                val firstX = mPoints[pointPosition].x + mScroolX - (aPartWidth * index)
                if (firstX < 0) {
                    lastShowPosition = firstShowPosition + index - 1
                    break
                }
                index++
            }
//            log("")
//            var index = 0
//            while (pointPosition > 0) {
//                val firstX = mPoints[pointPosition].x + mScroolX - (aPartWidth * index)
//                if (firstX > 0) {
//                    positionInfos.add(
//                        MChartPointPositionInfo(
//                            firstShowPosition + index,
//                            firstX + 6,
//                            mPoints[pointPosition - index].y
//                        )
//                    )
//                } else  break
//                index++
//            }
        } else {
            val firstPointX = blankWidth - aPartWidth + mScroolX + spotRadius
            firstShowPosition =
                if (mScroolX >= 0) 0
                else (abs(mScroolX) / aPartWidth).toInt() + 1

            var pointX: Float
            for (index in firstShowPosition until mPoints.size) {
                pointX = firstPointX + aPartWidth * index
                if (pointX > mWidth) {
                    lastShowPosition = index - 1
                    break
                }
            }
        }
    }

    private fun setPoints() {
        var index = 0
        if (chartType == CHART_COLUMN) {
            datas.forEach {
                val x =
                    if (isLeft) aPartWidth * index + startX + columnWidth / 2
                    else aPartWidth * index + startX + aPartWidth / 2
                val num = if (it.point == 0) 0.5f else it.point.toFloat()
                val y =
                    (chartHeight / this@MyChartView.maxNum) * (this@MyChartView.maxNum - num) + startY
                mPoints.add(PointF(x, y))
                index++
            }
        } else {
            datas.forEach {
                val x =
                    if (isLeft) aPartWidth * index + startX
                    else aPartWidth * index + startX + spotRadius

                val y =
                    (chartHeight / this@MyChartView.maxNum) * (this@MyChartView.maxNum - it.point) + startY
                mPoints.add(PointF(x, y))
                index++
            }
        }
    }

    private fun setColumnDatas() {
        bezierCurve.clear()
        mPoints.forEach { point ->
            ArrayList<PointF>().also {
                it.add(PointF(point.x - columnWidth / 2, endY))
                it.add(PointF(point.x - columnWidth / 2, point.y))
                it.add(PointF(point.x + columnWidth / 2, point.y))
                it.add(PointF(point.x + columnWidth / 2, endY))
                bezierCurve.add(it)
            }
        }
    }

    /*
    p1是否有有弧度，取决于前一个点的p2，p2是否有弧度要看下一个点是否比当前点大很多，小于20（这个数待验证）就不该有弧度
     */
    private fun setBezierDatas() {
        var index = 0
        var p1Radian = false
        bezierCurve.clear()
        mPoints.forEach { _ ->
            ArrayList<PointF>().also {
                bezierCurve.add(it)
                when (index) {
                    0 -> {
                        getBezierCurveValue(mPoints[index], mPoints[index + 1]) { p1, p2, p3 ->
                            val pp0 = PointF(mPoints[index].x, mPoints[index].y)
                            it.add(pp0)
                            it.add(p1)

                            getP2(index, p2) { pp3, newP1Radian ->
                                p1Radian = newP1Radian
                                it.add(pp3)
                            }
                            it.add(p3)
                        }
                    }
                    mPoints.size - 1 -> {}
                    else -> {
                        getBezierCurveValue(mPoints[index], mPoints[index + 1]) { p1, p2, p3 ->
                            val pp1 = PointF(mPoints[index].x, mPoints[index].y)
                            val pp2 =
                                if (p1Radian) {
                                    val currentMiddleY =
                                        abs(mPoints[index + 1].y - mPoints[index].y) / 2
                                    val previousMiddleY =
                                        abs(mPoints[index - 1].y - mPoints[index].y) / 2
                                    if (currentMiddleY > previousMiddleY) {
                                        previousMiddleY
                                    } else {
                                        currentMiddleY
                                    }.let { smallMiddleY ->
                                        val middleY =
                                            if (smallMiddleY > chartHeight / 5) chartHeight / 5 else smallMiddleY
                                        if (mPoints[index].y > mPoints[index + 1].y) {
                                            PointF(p1.x, mPoints[index].y - middleY)
                                        } else {
                                            PointF(p1.x, mPoints[index].y + middleY)
                                        }
                                    }
                                } else p1
                            it.add(pp1)
                            it.add(pp2)
                            getP2(index, p2) { pp3, newP1Radian ->
                                p1Radian = newP1Radian
                                it.add(pp3)
                            }
                            it.add(p3)
                        }
                    }
                }
            }
            index++
        }
    }

    private fun getLoopNums(list: List<Any>, foo: (Int, Int) -> Unit) {
        val onePosition =
            if (firstShowPosition < 1)
                0
            else {
                firstShowPosition - 1
            }.let {
                if (isLeft) {
                    mPoints.size - 1 - it
                } else it
            }
        val lastPosition =
            if (lastShowPosition > list.size - 2)
                list.size - 1
            else {
                lastShowPosition + 1
            }.let {
                if (isLeft) {
                    list.size - 1 - it
                } else it
            }

        val smallNum: Int
        val bigNum: Int
        if (onePosition > lastPosition) {
            smallNum = lastPosition
            bigNum = onePosition
        } else {
            smallNum = onePosition
            bigNum = lastPosition
        }
        foo.invoke(smallNum, bigNum)
    }

    private fun getP2(index: Int, p2: PointF, foo: (PointF, Boolean) -> Unit) {
        val p1Radian: Boolean
        val p3 =
            if (index < mPoints.size - 2) {
                val currentMiddleY = abs((mPoints[index + 1].y - mPoints[index + 2].y) / 2)
                val previousMiddleY = abs((mPoints[index + 1].y - mPoints[index].y) / 2)
                if (currentMiddleY > previousMiddleY) {
                    previousMiddleY
                } else {
                    currentMiddleY
                }.let {
                    if (mPoints[index].y > mPoints[index + 1].y && mPoints[index + 1].y > mPoints[index + 2].y) {//连续向上
                        p1Radian = true
                        PointF(p2.x, mPoints[index + 1].y + it)
                    } else if (mPoints[index].y < mPoints[index + 1].y && mPoints[index + 1].y < mPoints[index + 2].y) {//连续向下
                        p1Radian = true
                        PointF(p2.x, mPoints[index + 1].y - it)
                    } else {
                        p1Radian = false
                        p2
                    }
                }
            } else {
                p1Radian = false
                p2
            }
        foo.invoke(p3, p1Radian)
    }

    @SuppressLint("MissingPermission")
    private fun shock(foo: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.apply {
                if (hasVibrator()) {//判断硬件是否有振动器。
                    cancel()//关闭或者停止振动器。 VibrationEffect.DEFAULT_AMPLITUDE
                    vibrate(VibrationEffect.createOneShot(46, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
        }
        foo.invoke()
    }

    private fun getColor(id: Int): Int = context.resources.getColor(id)

    private fun log(str: String) {
        Log.d("MyChartView", str)
    }

    /**
     * Int      是数据源的position
     * Boolean  true是按下的回调，false是抬起的回调
     */
    fun setOnItemClickListener(itemClickListener: (Int, Boolean, Float, Float) -> Unit) {
        this.itemClickListener = itemClickListener
    }

    fun setDatas(list: ArrayList<MChartData>? = null) {
        datas.clear()
        mPoints.clear()
        if (list.isNullOrEmpty()) {
            this@MyChartView.maxNum = 100
            initFoundationSize()
            postInvalidate()
        } else {
            mScroolX = 0f
            datas.addAll(
                if (isLeft) list.reversed()
                else list
            )

            this@MyChartView.maxNum = list[0].maxNum
            initFoundationSize {
                setPoints()
                when (chartType) {//线的使用 mPoints 作为数据即可
                    CHART_COLUMN -> {
                        setColumnDatas()
                    }
                    CHART_BEZIER_CURVE -> {
                        setBezierDatas()
                    }
                }
            }
            itemClickListener?.apply { setFirstShowInfo() }
            if (isShowAnim) {
                animWidth =
                    mWidth - (textPaint.measureText("$maxNum") + margin + textAndLineClearance + linePaint.strokeWidth / 2)
                if (isAniming) animator?.cancel()
                animator?.start()
            } else postInvalidate()
        }
    }

    class MAnimatorListener(private val foo: (Boolean) -> Unit) : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            foo.invoke(true)
        }

        override fun onAnimationEnd(animation: Animator?) {
            foo.invoke(false)
        }

        override fun onAnimationCancel(animation: Animator?) {
            foo.invoke(false)
        }

        override fun onAnimationRepeat(animation: Animator?) {}
    }
}