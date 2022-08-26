package com.ntrade.demo.view.progress_button

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ntrade.demo.R

/**
 * 创建者：wanglei
 * 时间：2021/11/10 13:13
 * 类描述：拼色的进度按钮  豌豆荚下载按钮的样式
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
class ProgressButton : View {

    private var defaultBackgroundPaint: Paint? = null
    private var selectorBackgroundPaint: Paint? = null
    private var defaultTextPaint: Paint? = null
    private var selectorTextPaint: Paint? = null

    private var backgroundDefaultColor = 0//按钮的底色
    private var backgroundSelectorColor = 0//进度的底色
    private var defaultTextColor = 0//文字的颜色
    private var selectorTextColor = 0//进度中的文字的颜色
    private var textPaintWidth = 1f//字的每一笔的宽度，没有特殊需求最好不设置
    private var backgroundLineWidth = 0f//线的宽度
    private var radius = 0f//按钮四个角的弧度
    private var textSize = 0f//字的大小
    private var backgroundIsLine = false//没有进度时底色是线的还是填充满的，true是线

    private var mWidth = 0
    private var mHeight = 0
    private var progress = 0
    private var str: String = ""

    private var TAG = 0
    private val TAG_PROGRESS = 0
    private val TAG_TEXT = 1
    private val TAG_BACKGROUND = 2

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

    @SuppressLint("Recycle")
    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.ProgressButton).apply {
            backgroundDefaultColor = getColor(//按钮的底色
                R.styleable.ProgressButton_background_default_color,
                Color.parseColor("#0000FF")
            )

            backgroundSelectorColor = getColor(//进度的底色
                R.styleable.ProgressButton_background_selector_color,
                Color.parseColor("#3CB371")
            )

            defaultTextColor = getColor(//文字的颜色
                R.styleable.ProgressButton_text_default_color,
                Color.parseColor("#3CB371")
            )

            selectorTextColor = getColor(//进度中的文字的颜色
                R.styleable.ProgressButton_text_selector_color,
                Color.parseColor("#ffffff")
            )

            backgroundLineWidth =//线的宽度
                getDimension(R.styleable.ProgressButton_background_line_width, 10f)

            radius =//按钮四个角的弧度
                getDimension(R.styleable.ProgressButton_radius, 66f)

            textSize =//字的大小
                getDimension(R.styleable.ProgressButton_text_size, 50f)

            backgroundIsLine = //没有进度时底色是线的还是填充漫的，true是线
                getInt(R.styleable.ProgressButton_background_is_line, 2) == 1

            //字的每一笔的宽度，没有特殊需求最好不设置
            textPaintWidth = getFloat(R.styleable.ProgressButton_text_paint_width, 0f)
            if (textPaintWidth == 0f) {
                textPaintWidth = textSize * 0.6f
            }
            initDefaultBackgroundPaint()
            initSelectorBackgroundPaint()
            initSelectorTextPaint()
            initDefaultTextPaint()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w //控件的宽
        mHeight = h //控件的高
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.save()
        when (TAG) {
            TAG_PROGRESS -> {
                defaultBackgroundPaint?.setDefaultBackground(canvas)
                defaultTextPaint?.setProgressText(canvas)
                clip(canvas)
                selectorBackgroundPaint?.setSelectorBackground(canvas)
                selectorTextPaint?.setProgressText(canvas)
            }
            TAG_TEXT -> {
                defaultBackgroundPaint?.setDefaultBackground(canvas)
                defaultTextPaint?.setText(canvas)
                clip(canvas)
                selectorBackgroundPaint?.setSelectorBackground(canvas)
                selectorTextPaint?.setText(canvas)
            }
            TAG_BACKGROUND -> {
                selectorBackgroundPaint?.setSelectorBackground(canvas)
                selectorTextPaint?.setProgressText(canvas)
            }
        }
        canvas.restore()
    }

    fun setProgress(progress: Int) {
        if (progress > -1 && progress < 101) {
            this.progress = progress
            selectorBackgroundPaint?.color = backgroundSelectorColor
            TAG = TAG_PROGRESS
            invalidate()
        }
    }

    fun setText(str: String) {
        this.str = str
        TAG = TAG_TEXT
        invalidate()
    }

    fun setBackgroundResId(resid: Int) {
        TAG = TAG_BACKGROUND
        selectorBackgroundPaint?.color = ContextCompat.getColor(context, resid)
        invalidate()
    }

    private fun Paint.setText(canvas: Canvas) {
        val textX = mWidth / 2f - getDrawTextWidth(this, str) / 2f
        val textY = mHeight / 2f + getDrawTextHeight(this) / 2f - backgroundLineWidth
        canvas.drawText(str, textX, textY, this)
    }

    private fun Paint.setProgressText(canvas: Canvas) {
        val textX = mWidth / 2f - getDrawTextWidth(this, "$progress%") / 2f
        val textY = mHeight / 2f + getDrawTextHeight(this) / 2f - backgroundLineWidth
        canvas.drawText("$progress%", textX, textY, this)
    }

    /**
     * 设置擦除
     */
    private fun clip(canvas: Canvas) {
        val mPath = Path()
        mPath.addRoundRect(
            RectF(0f, 0f, mWidth.toFloat() * (progress.toFloat() / 100), mHeight.toFloat()),
            0f,//圆角的半径
            0f,
            Path.Direction.CW
        )
        canvas.clipPath(mPath)
    }


    private fun Paint.setSelectorBackground(canvas: Canvas) {
        val mPath = Path()
        mPath.addRoundRect(
            RectF(
                backgroundLineWidth,
                backgroundLineWidth,
                mWidth.toFloat() - backgroundLineWidth,
                mHeight.toFloat() - backgroundLineWidth
            ),
            radius,//圆角的半径
            radius,
            Path.Direction.CW
        )
        canvas.drawPath(mPath, this)
    }

    private fun Paint.setDefaultBackground(canvas: Canvas) {
        val mPath = Path()
        mPath.addRoundRect(
            RectF(
                backgroundLineWidth,
                backgroundLineWidth,
                mWidth.toFloat() - backgroundLineWidth,
                mHeight.toFloat() - backgroundLineWidth
            ),
            radius,//圆角的半径
            radius,
            Path.Direction.CW
        )
        canvas.drawPath(mPath, this)
    }

    private fun getDrawTextWidth(paint: Paint, text: String): Float {
        return paint.measureText(text)
    }

    private fun getDrawTextHeight(paint: Paint): Int {
        val fm = paint.fontMetricsInt
        return fm.top.inv() - (fm.top.inv() - fm.ascent.inv()) - (fm.bottom - fm.descent)
    }

    private fun initDefaultBackgroundPaint() {
        Paint().apply {
            isAntiAlias = true // 设置画笔的锯齿效果
            color = backgroundDefaultColor
            strokeWidth = backgroundLineWidth//设置画笔粗细
            style =
                if (backgroundIsLine) Paint.Style.STROKE
                else Paint.Style.FILL_AND_STROKE
            defaultBackgroundPaint = this
        }
    }

    private fun initSelectorBackgroundPaint() {
        Paint().apply {
            isAntiAlias = true // 设置画笔的锯齿效果
            color = backgroundSelectorColor
            strokeWidth = backgroundLineWidth//设置画笔粗细
            style = Paint.Style.FILL_AND_STROKE//填充
            selectorBackgroundPaint = this
        }
    }

    private fun initDefaultTextPaint() {
        Paint().apply {
            isAntiAlias = true // 设置画笔的锯齿效果
            color = defaultTextColor//给画笔设置颜色
            strokeWidth = textPaintWidth
            textSize = this@ProgressButton.textSize
            defaultTextPaint = this
        }
    }

    private fun initSelectorTextPaint() {
        Paint().apply { //实例化画笔对象
            selectorTextPaint = this
            isAntiAlias = true // 设置画笔的锯齿效果
            color = selectorTextColor//给画笔设置颜色
            strokeWidth = textPaintWidth
            textSize = this@ProgressButton.textSize
            defaultTextPaint = this
        }
    }
}