package com.ntrade.demo.view.test

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.ntrade.demo.R
import com.ntrade.demo.tool.PaintUtils
import kotlin.math.abs


/** * 创建者：leiwu
 * * 时间：2022/10/8 11:15
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class TestView : View {

    private var mWidth = -1f
    private var mHeight = -1f
    private var textHeight = -1f
    private val paintUtils by lazy { PaintUtils() }
    private val mPath by lazy { Path() }

    private val mPaint by lazy {
        getPaint(
            getColor(R.color.bessel_line),
            1,
            Paint.Style.STROKE
        ).apply {
            textSize = dp2px(20f).toFloat()
        }
    }

    private val str = "我是中国人"
    private val bitmap by lazy {
        scalingBitmap(BitmapFactory.decodeResource(resources, R.mipmap.aaa), mWidth, mHeight)
    }

    constructor(context: Context?) : super(context)
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
        textHeight = paintUtils.measureHeight(mPaint).toFloat()
    }

    val mmmmPaint by lazy { Paint().apply {
        strokeWidth = 30f
    } }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.apply {
//            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//            //计算基线位置，让文字居中
//            val baseline = height * 1.0f / 2 + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
//            //原始canvas中画文字
//            canvas.drawText("中奖了", width * 0.5f, baseline, textPaint);
//
//            //离屏绘制
//            int layer = canvas.saveLayer(0, 0, width, height, paint);
//
//            //绘制橡皮擦路径，图层混合后变为透明像素
//            canvas1.drawPath(path, pathPaint);
//            //绘制目标图像
//            canvas.drawBitmap(dstBitmap, 0, 0, paint);
//            //设置混合模式
//            paint.setXfermode(porterDuffXfermode);
//            //绘制源图像
//            canvas.drawBitmap(srcBitmap, null, srcRect, paint);
//            //清除混合模式
//            paint.setXfermode(null);
//
//            //恢复图层
//            canvas.restoreToCount(layer);
//
//        }
    }

    var isMove = true
    var mX = -1f
    var mY = -1f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
//                    isMove = true
//                    mX = x
//                    mY = y
                    mPath.moveTo(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
//                    if (isMove) {
//                        mPath.moveTo(mX, mY)
//                    } else {
//                        if (abs(x - mX) > 10 || abs(y - mY) > 10) {
//                            mPath.lineTo(mX, mX)
//                        }
//                    }

//                    if (isMove) {
//                        mPath.moveTo(mX, mY)
//                    } else {
                        mPath.lineTo(x, y)
//                    }
                    isMove = !isMove
                    mX = x
                    mY = y
                }
//                MotionEvent.ACTION_MOVE -> mPath.reset()
            }
            invalidate()
        }
        return true
    }

    private fun Canvas.test() {
        mPaint.color = getColor(R.color.bessel_line)
        mPath.reset()
        mPath.moveTo(50f, 50f)
        mPath.lineTo(400f, 50f)
        mPath.lineTo(400f, 200f)
        mPath.lineTo(50f, 200f)
        mPath.close()
        drawPath(mPath, mPaint)// 画一个矩形
//            canvas.drawText(str, 0f, textHeight, mPaint)

        mPaint.color = getColor(R.color.line_1966ff)
        mPath.reset()
        mPath.moveTo(50f, 50f)
        mPath.lineTo(250f, 50f)
        mPath.lineTo(250f, 125f)
        mPath.lineTo(50f, 125f)
        mPath.close()
        drawPath(mPath, mPaint)

        mPath.reset()
        mPath.moveTo(70f, 70f)
        mPath.lineTo(200f, 70f)
        mPath.lineTo(200f, 125f)
        mPath.lineTo(70f, 125f)
        mPath.close()

        save()
        clipPath(mPath)
        drawColor(Color.RED)
        drawText(str, 0f, 120f, mPaint)
        restore()

        mPath.reset()
        mPath.moveTo(70f, 570f)
        mPath.lineTo(200f, 570f)
        mPath.lineTo(200f, 725f)
        mPath.lineTo(70f, 725f)

        mPath.moveTo(70f, 870f)
        mPath.lineTo(200f, 870f)
        mPath.lineTo(200f, 1025f)
        mPath.lineTo(70f, 1025f)

        drawPath(mPath, mPaint)
    }

    private fun getColor(id: Int): Int = context.resources.getColor(id)

    private fun dp2px(value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        ).toInt()
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
     * 缩放bitmap，清晰度不变，尺寸改变（像素点总数改变）
     * @param bitmap 要缩放的bitmap
     * @param newWidth 要缩放的bitmap的宽
     * @param newHeight 要缩放的bitmap的高
     */
    fun scalingBitmap(bitmap: Bitmap, newWidth: Float, newHeight: Float): Bitmap? {
        // 获得图片的宽高
        val width = bitmap.width
        val height = bitmap.height
        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // 得到新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }
}