package com.ntrade.demo.view.handicap

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.ntrade.demo.R
import com.ntrade.demo.bean.HandicapData
import com.ntrade.demo.tool.PaintUtils

/** * 创建者：leiwu
 * * 时间：2022/9/26 09:50
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class HandicapView : View {

    private var textMarginLeft = 10f
    private var textMarginRight = 10f
    private var isLeft = true // 图向屏幕的左边是true ,否则是false
    private var total = 5
    private var defaultNum = 0.2f
    private var itemColor = -1
    private var defaultStr = "--"

    private var mWidth = -1f
    private var mHeight = -1f
    private var animTima = 128L
    private var aPartHeight = -1f
    private var mAnimValue = 0f
    private var textHeight = -1
    private var isAniming = false

    private var thread: Thread? = null
    private var sonThreadHandler: Handler? = null
    private val HANDLER_DESTROY = 11
    private val SET_DATAS = 12
    private val paintUtils by lazy { PaintUtils() }
    private val defaultData by lazy { ArrayList<HandicapData>() }
    private val drawMap by lazy { HashMap<Int, ArrayList<Float>>() }
    private val list by lazy { ArrayList<HandicapData>() }
    private val mPath by lazy { Path() }

    private val animator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = AccelerateInterpolator()
            addUpdateListener { animation ->
                mAnimValue = animation.animatedValue.toString().toFloat()
                postInvalidate()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    isAniming = true
                }

                override fun onAnimationEnd(animation: Animator?) {
                    isAniming = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isAniming = false
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            duration = animTima
        }
    }

    private val formPaint by lazy {
        getPaint(Color.BLACK, 2, Paint.Style.STROKE)
    }

    private val backgroundPaint by lazy {
        getPaint(itemColor, 1, Paint.Style.FILL_AND_STROKE)
    }

    private val textPaintLeft by lazy {
        getPaint(
            getColor(R.color.bessel_text),
            1,
            Paint.Style.FILL_AND_STROKE
        )
    }

    private val textPaintRight by lazy {
        getPaint(
            getColor(R.color.bessel_text),
            1,
            Paint.Style.FILL_AND_STROKE
        )
    }

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
        mHeight = h.toFloat()//控件的高
        aPartHeight = mHeight / total
        textHeight = paintUtils.measureHeight(textPaintLeft)
        if (defaultData.isNotEmpty())
            setDatas(defaultData)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (drawMap.isEmpty()) return
        canvas?.apply {
            try {
                drawItem()
                drawForm()
                setText()
            } catch (e: Exception) {
            }
        }
    }

    fun setDefaultDatas() {
        setDatas(defaultData)
    }

    fun setDatas(list: List<HandicapData>) {
        Message.obtain().let {
            it.what = SET_DATAS
            it.obj = list
            it
        }.apply {
            sonThreadHandler?.also {
                it.sendMessage(this)
            }
        }
    }

    fun viewDestroy() {
        sonThreadHandler?.sendEmptyMessage(HANDLER_DESTROY)
    }

    private fun Canvas.setText() {
        for (x in 0 until list.size) {
            val leftStr = list[x].leftStr
            val rightStr = list[x].rightStr
            val strY = aPartHeight * x + aPartHeight / 2 + textHeight / 2
            val strX = mWidth - (textPaintRight.measureText(rightStr) + textMarginRight)
            drawText(leftStr, textMarginLeft, strY, textPaintLeft)
            drawText(rightStr, strX, strY, textPaintRight)
        }
    }

    private fun Canvas.drawItem() {
        var startX: Float
        var startY: Float
        var stopX: Float
        var stopY: Float
        for (x in 0 until total) {
            mPath.reset()
            startY = aPartHeight * x
            stopY = aPartHeight * (x + 1)

            val list = drawMap[x]!!
            val w = list[0]
            if (list.size > 1)
                list.removeAt(0)

            if (isLeft) {
                startX = 0f
                stopX = w * mWidth
            } else {
                startX = mWidth
                stopX = mWidth - (w * mWidth)
            }
            mPath.moveTo(startX, startY)
            mPath.lineTo(stopX, startY)
            mPath.lineTo(stopX, stopY)
            mPath.lineTo(startX, stopY)
            mPath.close()
            drawPath(mPath, backgroundPaint)
        }
    }

    /**
     * 底格的横线
     */
    private fun Canvas.drawForm() {
        var startX: Float
        var startY: Float
        var stopX: Float
        var stopY: Float
        for (x in 1 until total) {//画横线
            startX = 0f
            startY = aPartHeight * x
            stopX = mWidth
            stopY = aPartHeight * x
            drawLine(startX, startY, stopX, stopY, formPaint)
        }
    }

    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttrs(attrs: AttributeSet?) {
        attrs?.apply {
            context.obtainStyledAttributes(this, R.styleable.handicap).apply {
                textMarginLeft = getDimension(R.styleable.handicap_text_margin_left, 10f)
                textMarginRight = getDimension(R.styleable.handicap_text_margin_right, 10f)
                textPaintLeft.textSize = getDimension(R.styleable.handicap_text_left_size, 10f)
                textPaintRight.textSize = getDimension(R.styleable.handicap_text_right_size, 10f)
                formPaint.strokeWidth = getDimension(R.styleable.handicap_split_line_height, 2f)

                isLeft = getBoolean(R.styleable.handicap_is_left_handicap, true)

                total = getInteger(R.styleable.handicap_item_total, 5)
                defaultNum = getInteger(R.styleable.handicap_item_default_num, 10) / 100f
                defaultStr = getString(R.styleable.handicap_default_str).let {
                    if (it.isNullOrEmpty()) "--" else it
                }
                itemColor = getColor(
                    R.styleable.handicap_item_color, 855681164
                )
                textPaintLeft.color = getColor(
                    R.styleable.handicap_text_left_color, -5658199
                )
                textPaintRight.color = getColor(
                    R.styleable.handicap_text_right_color, -16734068
                )
                formPaint.color = getColor(
                    R.styleable.handicap_split_line_color, -1
                )
                initData()
                threadCreate()
            }
        }
    }

    private fun initData() {
        if (defaultData.isEmpty()) {
            for (x in 0 until total) {
                defaultData.add(HandicapData(defaultNum, defaultStr, defaultStr))
            }
        }
        for (x in 0 until total) {
            val list = ArrayList<Float>()
            list.add(defaultNum)
            drawMap[x] = list
        }
    }

    private fun threadCreate() {
        Thread(MyRunable(object : MyInterface {
            override fun doSomething() {
                Looper.prepare()
                Looper.myLooper()?.apply {
                    sonThreadHandler = Handler(this) {
                        when (it.what) {
                            HANDLER_DESTROY -> {
                                sonThreadHandler?.removeCallbacksAndMessages(null)
                                sonThreadHandler = null
                                if (isAniming) animator?.cancel()
                                Looper.myLooper()?.quitSafely()
                            }
                            SET_DATAS -> setData(it.obj as List<HandicapData>)
                        }
                        true
                    }
                }
                Looper.loop()
            }
        })).also {
            thread = it
            it.start()
        }
    }

    private fun setData(list: List<HandicapData>) {
        if (isAniming) animator?.cancel()
        this.list.clear()
        this.list.addAll(list)
        try {
            for (x in 0 until total) {
                drawMap[x]!!.also { oldList ->
                    val oldNum = oldList[0]
                    val frequency = (animTima / 16f).toInt()
                    oldList.clear()
                    ((list[x].num - oldNum) / frequency).also {
                        for (y in 1..frequency) {
                            oldList.add(oldNum + it * y)
                        }
                    }
                }
            }
            animator?.start()
        } catch (e: Exception) {
            Log.e("", "${e.message}")
        }
    }

    private fun getColor(id: Int): Int = context.resources.getColor(id)

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

    private fun dp2px(value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun Px2Dp(px: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f)
    }
}