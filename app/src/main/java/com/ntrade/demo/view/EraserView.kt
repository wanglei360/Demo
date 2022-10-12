package com.ntrade.demo.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.ntrade.demo.R
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * 创建者：leiwu
 *  时间：2022/10/8 16:02
 *  类描述：
 *  修改人：
 *  修改时间：
 *  修改备注：
 */
class EraserView : View {
    //控件宽高
    private var mWidth = 0f
    private var mHeight = 0f
    private var mX = -1f
    private var mY = -1f

    private val mPath by lazy { Path() }

    // 遮罩层
    private val srcBitmap by lazy {
        scalingBitmap(BitmapFactory.decodeResource(resources, R.mipmap.bbb), mWidth, mHeight)
    }

    // 底图
    private val backgroundBitmap by lazy {
        scalingBitmap(BitmapFactory.decodeResource(resources, R.mipmap.aaa), mWidth, mHeight)
    }

    //橡皮擦paint
    private val pathPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.color_red)
            style = Paint.Style.STROKE
            strokeWidth = 50f
        }
    }

    //文字paint
    private val textPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.color_red)
            textAlign = Paint.Align.CENTER
            textSize = 100f
            isFakeBoldText = true
        }
    }

    //混合模式,清除指定路径上的像素点
    //http://t.zoukankan.com/krislight1105-p-5079777.html  里面的图可以参考还有说明
    private val mXfermodeNew by lazy { PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //设置控件宽高
        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            // 在原始图层画一个图案作为底图,露出的就是这个
            drawBitmap(backgroundBitmap!!, 0f, 0f, textPaint)

            // 创建一个新图层
            val layer = saveLayer(0f, 0f, mWidth, mHeight, pathPaint)
            // 在新图层上绘制蒙版
            drawBitmap(srcBitmap!!, 0f, 0f, pathPaint)
            // 设置模式，http://t.zoukankan.com/krislight1105-p-5079777.html  里面的图可以参考
            // 此模式下，与蒙版重合的地方像素会透明
            pathPaint.xfermode = mXfermodeNew
            //绘制橡皮擦路径，清除新图层指定路径上的像素点，
            drawPath(mPath, pathPaint)
            //清除混合模式
            pathPaint.xfermode = null
            //恢复图层
            restoreToCount(layer)//退出图层操作
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mX = event.x
                mY = event.y
                mPath.moveTo(mX, mY)
            }
            MotionEvent.ACTION_MOVE -> {
                //只有挪动10像素以上才往path中存数据，减少path的存储数据的量
                if (abs(event.x - mX) > 10 || abs(event.y - mY) > 10) {
                    mX = event.x
                    mY = event.y
                    mPath.lineTo(mX, mY)
                }
            }
        }
        invalidate()
        return true
    }

    /**
     * 当 View 与 Window 分离的时候会回调 onDetachedFromWindow 。
     */
    override fun onDetachedFromWindow() {
//        pathPaint = null
//        paint = null
//        textPaint = null
        super.onDetachedFromWindow()
    }

    /**
     * 清除目标图像绘制路径，遮罩层就恢复了
     */
    fun restore() {
        mPath.reset()
        invalidate()
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
        val scaleWidth = newWidth / width
        val scaleHeight = newHeight / height
        val scale =
            if (scaleWidth > scaleHeight) {
                scaleWidth
            } else scaleHeight
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        // 得到新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }
}